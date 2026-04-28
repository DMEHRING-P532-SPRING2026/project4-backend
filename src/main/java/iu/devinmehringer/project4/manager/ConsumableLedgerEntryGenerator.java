package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.manager.engine.OverConsumptionPostingRule;
import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;
import iu.devinmehringer.project4.model.ledger.Transaction;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ConsumableLedgerEntryGenerator extends AbstractLedgerEntryGenerator {

    public ConsumableLedgerEntryGenerator(OverConsumptionPostingRule postingRuleEngine) {
        setPostingRuleEngine(postingRuleEngine);
    }

    @Override
    protected List<ResourceAllocation> selectAllocations(ImplementedAction action) {
        return action.getProposedAction().getAllocations().stream()
                .filter(a -> a.getResourceType().getKind() == ResourceTypeKind.CONSUMABLE)
                .toList();
    }

    @Override
    protected void validate(List<ResourceAllocation> allocs) {
        for (ResourceAllocation a : allocs) {
            if (a.getResourceType().getPoolAccount() == null) {
                throw new IllegalStateException(
                        "ResourceType '" + a.getResourceType().getName()
                                + "' has no pool account");
            }
            if (a.getQuantity() == null || a.getQuantity().signum() <= 0) {
                throw new IllegalStateException(
                        "Allocation quantity must be positive for resource '"
                                + a.getResourceType().getName() + "'");
            }
        }
    }

    @Override
    protected void afterPost(Transaction tx) {
        if (!tx.isBalanced()) {
            throw new IllegalStateException(
                    "Transaction is not balanced — entries do not sum to zero");
        }
    }
}