package iu.devinmehringer.project4.model.resource;

import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "resource_allocation")
public class ResourceAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_allocation_seq")
    @SequenceGenerator(
            name = "resource_allocation_seq",
            sequenceName = "resource_allocation_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_type_id", nullable = false)
    private ResourceType resourceType;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceAllocationKind kind;

    private String assetId;
    private Instant timePeriodStart;
    private Instant timePeriodEnd;

    @ManyToOne
    @JoinColumn(name = "proposed_action_id")
    private ProposedAction proposedAction;

    @ManyToOne
    @JoinColumn(name = "implemented_action_id")
    private ImplementedAction implementedAction;

    public ResourceAllocation() {}

    public Long getId() { return id; }

    public ResourceType getResourceType() { return resourceType; }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public BigDecimal getQuantity() { return quantity; }

    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public ResourceAllocationKind getKind() { return kind; }

    public void setKind(ResourceAllocationKind kind) { this.kind = kind; }

    public String getAssetId() { return assetId; }

    public void setAssetId(String assetId) { this.assetId = assetId; }

    public Instant getTimePeriodStart() { return timePeriodStart; }

    public void setTimePeriodStart(Instant timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    public Instant getTimePeriodEnd() { return timePeriodEnd; }

    public void setTimePeriodEnd(Instant timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }

    public ProposedAction getProposedAction() { return proposedAction; }

    public void setProposedAction(ProposedAction proposedAction) {
        this.proposedAction = proposedAction;
    }

    public ImplementedAction getImplementedAction() { return implementedAction; }

    public void setImplementedAction(ImplementedAction implementedAction) {
        this.implementedAction = implementedAction;
    }
}
