package iu.devinmehringer.project4.manager.engine;

import iu.devinmehringer.project4.access.EntryAccess;
import iu.devinmehringer.project4.access.PostingRuleAccess;
import iu.devinmehringer.project4.access.TransactionAccess;
import iu.devinmehringer.project4.model.ledger.PostingRule;
import iu.devinmehringer.project4.model.ledger.PostingRuleStrategyType;
import iu.devinmehringer.project4.model.ledger.Transaction;
import iu.devinmehringer.project4.model.resource.Account;
import iu.devinmehringer.project4.model.resource.AccountKind;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
public class OverConsumptionPostingRule {

    private final PostingRuleAccess postingRuleAccess;
    private final EntryAccess entryAccess;
    private final TransactionAccess transactionAccess;

    public OverConsumptionPostingRule(
            PostingRuleAccess postingRuleAccess,
            EntryAccess entryAccess,
            TransactionAccess transactionAccess) {
        this.postingRuleAccess = postingRuleAccess;
        this.entryAccess = entryAccess;
        this.transactionAccess = transactionAccess;
    }

    public void fireForAccount(Account account, Transaction sourceTx) {
        if (account.getAccountKind() != AccountKind.POOL) return;

        BigDecimal balance = entryAccess.getBalanceForAccount(account);
        if (balance.compareTo(BigDecimal.ZERO) >= 0) return;

        List<PostingRule> rules = postingRuleAccess.findByTriggerAccount(account);

        for (PostingRule rule : rules) {
            if (rule.getStrategyType() != PostingRuleStrategyType.OVER_CONSUMPTION_ALERT) {
                continue;
            }

            Transaction alertTx = new Transaction();
            alertTx.setDescription("Over-consumption alert for account: "
                    + account.getName());
            alertTx.setOriginatingAction(sourceTx.getOriginatingAction());

            Instant now = Instant.now();
            alertTx.createEntry(
                    rule.getOutputAccount(),
                    balance.abs(),
                    now,
                    now
            );

            transactionAccess.save(alertTx);
        }
    }
}