package iu.devinmehringer.project4.model.ledger;

import iu.devinmehringer.project4.model.resource.Account;
import jakarta.persistence.*;

@Entity
@Table(name = "posting_rule")
public class PostingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posting_rule_seq")
    @SequenceGenerator(
            name = "posting_rule_seq",
            sequenceName = "posting_rule_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trigger_account_id", nullable = false)
    private Account triggerAccount;

    @ManyToOne
    @JoinColumn(name = "output_account_id", nullable = false)
    private Account outputAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostingRuleStrategyType strategyType;

    public PostingRule() {}

    public Long getId() { return id; }

    public Account getTriggerAccount() { return triggerAccount; }

    public void setTriggerAccount(Account triggerAccount) {
        this.triggerAccount = triggerAccount;
    }

    public Account getOutputAccount() { return outputAccount; }

    public void setOutputAccount(Account outputAccount) {
        this.outputAccount = outputAccount;
    }

    public PostingRuleStrategyType getStrategyType() { return strategyType; }

    public void setStrategyType(PostingRuleStrategyType strategyType) {
        this.strategyType = strategyType;
    }
}