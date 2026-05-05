package iu.devinmehringer.project4.model.plan;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "suspension")
public class Suspension {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "suspension_seq")
    @SequenceGenerator(
            name = "suspension_seq",
            sequenceName = "suspension_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposed_action_id", nullable = false)
    private ProposedAction proposedAction;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ActionStateEnum previousState;

    public Suspension() {}

    public boolean isActive() {
        return endDate == null || endDate.isAfter(LocalDate.now());
    }

    public Long getId() { return id; }

    public ProposedAction getProposedAction() { return proposedAction; }
    public void setProposedAction(ProposedAction proposedAction) {
        this.proposedAction = proposedAction;
    }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public ActionStateEnum getPreviousState() { return previousState; }

    public void setPreviousState(ActionStateEnum previousState) {
        this.previousState = previousState;
    }
}