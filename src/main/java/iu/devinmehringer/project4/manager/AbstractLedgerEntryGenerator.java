package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.manager.engine.OverConsumptionPostingRule;
import iu.devinmehringer.project4.model.ledger.Entry;
import iu.devinmehringer.project4.model.ledger.Transaction;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Transactional
public abstract class AbstractLedgerEntryGenerator {

    public void setPostingRuleEngine(OverConsumptionPostingRule engine) {
    }

    public final Transaction generateEntries(ImplementedAction action) {
        List<ResourceAllocation> allocs = selectAllocations(action);
        if (allocs.isEmpty()) {
            return null;
        }
        validate(allocs);
        Transaction tx = createTransaction(action);
        for (ResourceAllocation a : allocs) {
            Entry withdrawal = buildWithdrawal(tx, a);
            Entry deposit = buildDeposit(tx, a);
            postEntries(tx, withdrawal, deposit);
        }
        afterPost(tx);
        return tx;
    }

    protected abstract List<ResourceAllocation> selectAllocations(
            ImplementedAction action);

    protected abstract void validate(List<ResourceAllocation> allocs);

    protected Entry buildWithdrawal(Transaction tx, ResourceAllocation a) {
        Instant now = Instant.now();
        return tx.createEntry(
                a.getResourceType().getPoolAccount(),
                a.getQuantity().negate(),
                tx.getOriginatingAction().getActualStart(),
                now
        );
    }

    protected Entry buildDeposit(Transaction tx, ResourceAllocation a) {
        Instant now = Instant.now();
        return tx.createEntry(
                tx.getOriginatingAction().getUsageAccount(),
                a.getQuantity(),
                tx.getOriginatingAction().getActualStart(),
                now
        );
    }

    protected void afterPost(Transaction tx) {}

    private Transaction createTransaction(ImplementedAction action) {
        Transaction tx = new Transaction();
        tx.setDescription("Completion of action: "
                + action.getProposedAction().getName());
        tx.setOriginatingAction(action);
        return tx;
    }

    private void postEntries(Transaction tx, Entry withdrawal, Entry deposit) {
        boolean balanced = tx.getEntries().stream()
                .map(Entry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .compareTo(BigDecimal.ZERO) == 0;

        if (!balanced) {
            throw new IllegalStateException(
                    "Transaction is not balanced after posting entries");
        }
    }
}