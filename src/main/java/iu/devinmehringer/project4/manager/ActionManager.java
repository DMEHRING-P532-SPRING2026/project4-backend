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
import iu.devinmehringer.project4.model.log.AuditLogEntry;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNode;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import iu.devinmehringer.project4.model.resource.Account;
import iu.devinmehringer.project4.model.resource.AccountKind;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import iu.devinmehringer.project4.model.resource.ResourceAllocationKind;
import iu.devinmehringer.project4.statemachine.ActionContext;
import iu.devinmehringer.project4.statemachine.ActionStateMachine;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ActionManager {

    private final ProposedActionAccess proposedActionAccess;
    private final ActionStateMachine stateMachine;
    private final ResourceTypeAccess resourceTypeAccess;
    private final PlanManager planManager;
    private final ConsumableLedgerEntryGenerator consumableLedgerEntryGenerator;
    private final AssetLedgerEntryGenerator assetLedgerEntryGenerator;
    private final ImplementedActionAccess implementedActionAccess;
    private final PlanAccess planAccess;
    private final AccountAccess accountAccess;
    private final TransactionAccess transactionAccess;
    private final EntryAccess entryAccess;
    private final OverConsumptionPostingRule overConsumptionPostingRule;
    private final AuditLogAccess auditLogAccess;
    private final ResourceAllocationAccess resourceAllocationAccess;

    public ActionManager(
            ProposedActionAccess proposedActionAccess,
            ActionStateMachine stateMachine,
            ResourceTypeAccess resourceTypeAccess,
            PlanManager planManager,
            PlanAccess planAccess,
            ConsumableLedgerEntryGenerator consumableLedgerEntryGenerator,
            AssetLedgerEntryGenerator assetLedgerEntryGenerator,
            ImplementedActionAccess implementedActionAccess,
            TransactionAccess transactionAccess,
            AccountAccess accountAccess,
            EntryAccess entryAccess,
            OverConsumptionPostingRule overConsumptionPostingRule,
            AuditLogAccess auditLogAccess,
            ResourceAllocationAccess resourceAllocationAccess) {
        this.proposedActionAccess = proposedActionAccess;
        this.stateMachine = stateMachine;
        this.resourceTypeAccess = resourceTypeAccess;
        this.planManager = planManager;
        this.planAccess = planAccess;
        this.consumableLedgerEntryGenerator = consumableLedgerEntryGenerator;
        this.assetLedgerEntryGenerator = assetLedgerEntryGenerator;
        this.implementedActionAccess = implementedActionAccess;
        this.transactionAccess = transactionAccess;
        this.accountAccess = accountAccess;
        this.entryAccess = entryAccess;
        this.overConsumptionPostingRule = overConsumptionPostingRule;
        this.auditLogAccess = auditLogAccess;
        this.resourceAllocationAccess = resourceAllocationAccess;
    }

    public ProposedAction getAction(Long id) {
        return proposedActionAccess.getProposedAction(id)
                .orElseThrow(() -> new ProposedActionNotFoundException(id.toString()));
    }

    public ProposedAction implement(Long id, ImplementRequest request) {
        ProposedAction action = getAction(id);
        if (request != null && action.getImplementedAction() != null) {
            ImplementedAction implemented = action.getImplementedAction();
            if (request.getActualParty() != null) {
                implemented.setActualParty(request.getActualParty());
            }
            if (request.getActualLocation() != null) {
                implemented.setActualLocation(request.getActualLocation());
            }
            implementedActionAccess.save(implemented);
        }

        return proposedActionAccess.save(action);
    }

    public ProposedAction submitForApproval(Long id) {
        ProposedAction action = getAction(id);
        validateDependenciesSatisfied(action);
        stateMachine.submitForApproval(action, new ActionContext(action, this));
        return proposedActionAccess.save(action);
    }

    public ProposedAction approve(Long id) {
        ProposedAction action = getAction(id);
        validateDependenciesSatisfied(action);

        Account usageAccount = new Account();
        usageAccount.setName("Usage - " + action.getName());
        usageAccount.setAccountKind(AccountKind.USAGE);

        ImplementedAction implemented = new ImplementedAction();
        implemented.setProposedAction(action);
        implemented.setActualStart(Instant.now());
        implemented.setActualParty(action.getParty());
        implemented.setActualLocation(action.getLocation());
        implemented.setUsageAccount(usageAccount);

        implementedActionAccess.save(implemented);
        action.setImplementedAction(implemented);

        stateMachine.approve(action, new ActionContext(action, this));
        return proposedActionAccess.save(action);
    }

    public ProposedAction reject(Long id) {
        ProposedAction action = getAction(id);
        stateMachine.reject(action, new ActionContext(action, this));
        return proposedActionAccess.save(action);
    }

    public ProposedAction reopen(Long id) {
        ProposedAction action = getAction(id);

        ImplementedAction implemented = action.getImplementedAction();
        if (implemented != null) {
            List<ResourceAllocation> consumable = action.getAllocations().stream()
                    .filter(a -> a.getResourceType().getKind()
                            == ResourceTypeKind.CONSUMABLE)
                    .toList();

            if (!consumable.isEmpty()) {
                Transaction reversalTx = createReversalTransaction(
                        implemented, consumable);
                transactionAccess.save(reversalTx);
            }
        }

        stateMachine.reopen(action, new ActionContext(action, this));
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
        ImplementedAction implemented = implementedActionAccess
                .findByProposedAction(action)
                .orElse(null);


        if (implemented != null) {
            implemented.setProposedAction(action);

            List<ResourceAllocation> allAllocations =
                    resourceAllocationAccess.findByProposedActionId(id);
            List<ResourceAllocation> consumable = allAllocations.stream()
                    .filter(a -> a.getResourceType().getKind()
                            == ResourceTypeKind.CONSUMABLE)
                    .toList();

            if (!consumable.isEmpty()) {
                try {
                    Transaction tx = consumableLedgerEntryGenerator
                            .generateEntries(implemented);
                    if (tx != null) {
                        tx.setResourceTypeKind(ResourceTypeKind.CONSUMABLE);
                        Transaction saved = transactionAccess.save(tx);
                        consumable.forEach(a ->
                                overConsumptionPostingRule.fireForAccount(
                                        a.getResourceType().getPoolAccount(), saved));
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
            List<ResourceAllocation> assets = allAllocations.stream()
                    .filter(a -> a.getResourceType().getKind() == ResourceTypeKind.ASSET
                            && a.getKind() == ResourceAllocationKind.SPECIFIC)
                    .toList();

            if (!assets.isEmpty()) {
                try {
                    Transaction assetTx = assetLedgerEntryGenerator
                            .generateEntries(implemented);
                    if (assetTx != null) {
                        assetTx.setResourceTypeKind(ResourceTypeKind.ASSET);
                        Transaction saved = transactionAccess.save(assetTx);
                    }
                } catch (Exception e) {
                    throw e;
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

    public List<AuditLogEntry> getAuditLog() {
        return auditLogAccess.getAll();
    }

    private Transaction createReversalTransaction(
            ImplementedAction implemented,
            List<ResourceAllocation> consumable) {
        Transaction tx = new Transaction();
        tx.setDescription("Reversal of action: "
                + implemented.getProposedAction().getName());
        tx.setOriginatingAction(implemented);

        Instant now = Instant.now();
        for (ResourceAllocation a : consumable) {
            tx.createEntry(
                    a.getResourceType().getPoolAccount(),
                    a.getQuantity(),
                    now,
                    now
            );
            tx.createEntry(
                    implemented.getUsageAccount(),
                    a.getQuantity().negate(),
                    now,
                    now
            );
        }
        return tx;
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

    private void validateNodeDependencies(Protocol nodeProtocol, String nodeName,
                                          Plan plan) {
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
            if (depNode != null
                    && depNode.getStatus() != ActionStateEnum.COMPLETED) {
                throw new IllegalStateTransitionException(
                        nodeName,
                        "dependency '" + depNode.getName()
                                + "' is currently " + depNode.getStatus()
                                + " — must be COMPLETED first"
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