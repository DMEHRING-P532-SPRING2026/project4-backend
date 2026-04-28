package iu.devinmehringer.project4.model.plan;

import iu.devinmehringer.project4.model.resource.Account;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "implemented_action")
public class ImplementedAction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "implemented_action_seq")
    @SequenceGenerator(
            name = "implemented_action_seq",
            sequenceName = "implemented_action_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "proposed_action_id")
    private ProposedAction proposedAction;  // optional per chapter 8

    @Column(nullable = false)
    private Instant actualStart;

    private String actualParty;

    private String actualLocation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usage_account_id")
    private Account usageAccount;

    public ImplementedAction() {}

    public Long getId() { return id; }

    public ProposedAction getProposedAction() { return proposedAction; }

    public void setProposedAction(ProposedAction proposedAction) {
        this.proposedAction = proposedAction;
    }

    public Instant getActualStart() { return actualStart; }

    public void setActualStart(Instant actualStart) {
        this.actualStart = actualStart;
    }

    public String getActualParty() { return actualParty; }

    public void setActualParty(String actualParty) {
        this.actualParty = actualParty;
    }

    public String getActualLocation() { return actualLocation; }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public Account getUsageAccount() { return usageAccount; }

    public void setUsageAccount(Account usageAccount) {
        this.usageAccount = usageAccount;
    }
}