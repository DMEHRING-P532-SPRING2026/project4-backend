package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.access.*;
import iu.devinmehringer.project4.controller.dto.account.AccountResponse;
import iu.devinmehringer.project4.controller.dto.action.ActionResponse;
import iu.devinmehringer.project4.controller.dto.action.ImplementRequest;
import iu.devinmehringer.project4.controller.dto.action.ResourceAllocationRequest;
import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.controller.exception.ProposedActionNotFoundException;
import iu.devinmehringer.project4.manager.engine.OverConsumptionPostingRule;
import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;
import iu.devinmehringer.project4.model.ledger.Entry;
import iu.devinmehringer.project4.model.ledger.Transaction;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNode;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import iu.devinmehringer.project4.model.resource.Account;
import iu.devinmehringer.project4.model.resource.AccountKind;
import iu.devinmehringer.project4.model.resource.ActionStateMachine;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import iu.devinmehringer.project4.statemachine.ActionContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ActionManager {

    private final ProposedActionAccess proposedActionAccess;
    private final ActionStateMachine stateMachine;
    private final ResourceTypeAccess resourceTypeAccess;
    private final PlanManager planManager;
    private final ConsumableLedgerEntryGenerator ledgerEntryGenerator;
    private final ImplementedActionAccess implementedActionAccess;
    private final PlanAccess planAccess;
    private final AccountAccess accountAccess;
    private final TransactionAccess transactionAccess;
    private final EntryAccess entryAccess;
    private final OverConsumptionPostingRule overConsumptionPostingRule;

    public ActionManager(
            ProposedActionAccess proposedActionAccess,
            ActionStateMachine stateMachine,
            ResourceTypeAccess resourceTypeAccess,
            PlanManager planManager,
            PlanAccess planAccess,
            ConsumableLedgerEntryGenerator ledgerEntryGenerator,
            ImplementedActionAccess implementedActionAccess,
            TransactionAccess transactionAccess,
            AccountAccess accountAccess,
            EntryAccess entryAccess,
            OverConsumptionPostingRule overConsumptionPostingRule) {
        this.proposedActionAccess = proposedActionAccess;
        this.stateMachine = stateMachine;
        this.resourceTypeAccess = resourceTypeAccess;
        this.planManager = planManager;
        this.planAccess = planAccess;
        this.ledgerEntryGenerator = ledgerEntryGenerator;
        this.implementedActionAccess = implementedActionAccess;
        this.transactionAccess = transactionAccess;
        this.accountAccess = accountAccess;
        this.entryAccess = entryAccess;
        this.overConsumptionPostingRule = overConsumptionPostingRule;
    }

    public ProposedAction getAction(Long id) {
        return proposedActionAccess.getProposedAction(id)
                .orElseThrow(() -> new ProposedActionNotFoundException(id.toString()));
    }

    public ProposedAction implement(Long id, ImplementRequest request) {
        ProposedAction action = getAction(id);
        validateDependenciesSatisfied(action);

        ImplementedAction implemented = new ImplementedAction();
        implemented.setProposedAction(action);
        implemented.setActualStart(Instant.now());

        if (request != null) {
            implemented.setActualParty(request.getActualParty());
            implemented.setActualLocation(request.getActualLocation());
        }

        Account usageAccount = new Account();
        usageAccount.setName("Usage - " + action.getName());
        usageAccount.setAccountKind(AccountKind.USAGE);
        implemented.setUsageAccount(usageAccount);

        implementedActionAccess.save(implemented);

        stateMachine.implement(action, new ActionContext(action, this));
        return proposedActionAccess.save(action);
    }

    public ProposedAction suspend(Long id, String reason) {
        ProposedAction action = getAction(id);
        stateMachine.suspend(action, new ActionContext(action, this), reason);
        return proposedActionAccess.save(action);
    }

    public ProposedAction resume(Long id) {
        ProposedAction action = getAction(id);
        stateMachine.resume(action, new ActionContext(action, this));
        return proposedActionAccess.save(action);
    }

    public ProposedAction complete(Long id) {
        ProposedAction action = getAction(id);

        ImplementedAction implemented = action.getImplementedAction();
        if (implemented != null) {
            List<ResourceAllocation> consumable = action.getAllocations().stream()
                    .filter(a -> a.getResourceType().getKind()
                            == ResourceTypeKind.CONSUMABLE)
                    .toList();

            if (!consumable.isEmpty()) {
                Transaction tx = ledgerEntryGenerator.generateEntries(implemented);
                if (tx != null) {
                    // save first so entries are visible to balance query
                    transactionAccess.save(tx);

                    // fire posting rules eagerly after save per F8
                    consumable.forEach(a ->
                            overConsumptionPostingRule.fireForAccount(
                                    a.getResourceType().getPoolAccount(), tx));
                }
            }
        }

        stateMachine.complete(action, new ActionContext(action, this));
        ProposedAction saved = proposedActionAccess.save(action);

        Plan plan = action.getParent();
        if (plan != null) {
            checkPlanCompleted(plan);
        }

        return saved;
    }

    public ProposedAction abandon(Long id) {
        ProposedAction action = getAction(id);
        stateMachine.abandon(action, new ActionContext(action, this));
        return proposedActionAccess.save(action);
    }

    public ProposedAction allocate(Long id, ResourceAllocationRequest request) {
        ProposedAction action = getAction(id);

        ResourceType resourceType = resourceTypeAccess.getResourceType(
                        request.getResourceTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ResourceType not found: " + request.getResourceTypeId()));

        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setResourceType(resourceType);
        allocation.setQuantity(request.getQuantity());
        allocation.setKind(request.getKind());
        allocation.setAssetId(request.getAssetId());
        allocation.setTimePeriodStart(request.getTimePeriodStart());
        allocation.setTimePeriodEnd(request.getTimePeriodEnd());
        allocation.setProposedAction(action);

        action.getAllocations().add(allocation);

        return proposedActionAccess.save(action);
    }

    public List<ActionResponse.AllocationResponse> getAllocations(Long id) {
        ProposedAction action = getAction(id);
        return action.getAllocations().stream()
                .map(allocation -> {
                    ActionResponse.AllocationResponse response =
                            new ActionResponse.AllocationResponse();
                    response.setId(allocation.getId());
                    response.setResourceTypeId(
                            allocation.getResourceType().getId());
                    response.setResourceTypeName(
                            allocation.getResourceType().getName());
                    response.setQuantity(allocation.getQuantity());
                    response.setKind(allocation.getKind().name());
                    response.setAssetId(allocation.getAssetId());
                    return response;
                })
                .toList();
    }

    public List<AccountResponse> getAccountsWithBalances() {
        return accountAccess.getAccounts().stream()
                .map(account -> {
                    BigDecimal balance = entryAccess.getBalanceForAccount(account);
                    return AccountResponse.from(account, balance);
                })
                .toList();
    }

    public List<Entry> getEntriesForAccount(Long accountId) {
        Account account = accountAccess.getAccount(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + accountId));
        return entryAccess.getEntriesForAccount(account);
    }

    private void checkPlanCompleted(Plan plan) {
        if (plan.getStatus() == ActionStateEnum.COMPLETED) {
            planAccess.save(plan);
            if (plan.getParent() != null) {
                checkPlanCompleted(plan.getParent());
            }
        }
    }

    private void validateDependenciesSatisfied(ProposedAction action) {
        Plan plan = action.getParent();
        if (plan == null) return;

        validatePlanHierarchy(plan);
        validateNodeDependencies(action.getProtocol(), action.getName(), plan);
    }

    private void validatePlanHierarchy(Plan plan) {
        Plan parent = plan.getParent();
        if (parent == null) return;

        validateNodeDependencies(plan.getSourceProtocol(), plan.getName(), parent);
        validatePlanHierarchy(parent);
    }

    private void validateNodeDependencies(Protocol nodeProtocol, String nodeName, Plan plan) {
        Protocol protocol = plan.getSourceProtocol();
        if (protocol == null || nodeProtocol == null) return;

        Optional<ProtocolStep> matchingStep = protocol.getProtocolSteps()
                .stream()
                .filter(Objects::nonNull)
                .filter(step -> step.getReferencedProtocol().equals(nodeProtocol))
                .findFirst();

        if (matchingStep.isEmpty()) return;

        Map<Protocol, PlanNode> nodeMap = buildNodeMap(plan);

        for (ProtocolStep dep : matchingStep.get().getDependsOn()) {
            PlanNode depNode = nodeMap.get(dep.getReferencedProtocol());
            if (depNode != null && depNode.getStatus() != ActionStateEnum.COMPLETED) {
                throw new IllegalStateTransitionException(
                        nodeName,
                        "dependency '" + depNode.getName()
                                + "' must be completed first"
                );
            }
        }
    }

    private Map<Protocol, PlanNode> buildNodeMap(Plan plan) {
        Map<Protocol, PlanNode> nodeMap = new HashMap<>();
        Iterator<PlanNode> iterator = planManager.getIterator(plan);
        while (iterator.hasNext()) {
            PlanNode node = iterator.next();
            if (node instanceof ProposedAction a && a.getProtocol() != null) {
                nodeMap.put(a.getProtocol(), a);
            } else if (node instanceof Plan p && p.getSourceProtocol() != null) {
                nodeMap.put(p.getSourceProtocol(), p);
            }
        }
        return nodeMap;
    }
}