package iu.devinmehringer.project4.model.plan;

import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "proposed_action")
public class ProposedAction implements PlanNode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposed_action_seq")
    @SequenceGenerator(
            name = "proposed_action_seq",
            sequenceName = "proposed_action_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "protocol_id")
    private Protocol protocol;

    private String party;

    private String timeRef;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStateEnum state = ActionStateEnum.PROPOSED;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Plan parent;

    @OneToMany(mappedBy = "proposedAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceAllocation> allocations = new ArrayList<>();

    @OneToMany(mappedBy = "proposedAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Suspension> suspensions = new ArrayList<>();

    @OneToOne(mappedBy = "proposedAction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ImplementedAction implementedAction;;


    public ProposedAction() {}

    @Override
    public Long getId() { return id; }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Protocol getProtocol() { return protocol; }

    public void setProtocol(Protocol protocol) { this.protocol = protocol; }

    public String getParty() { return party; }

    public void setParty(String party) { this.party = party; }

    public String getTimeRef() { return timeRef; }

    public void setTimeRef(String timeRef) { this.timeRef = timeRef; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public ActionStateEnum getState() { return state; }

    public void setState(ActionStateEnum state) { this.state = state; }

    public Plan getParent() { return parent; }

    public void setParent(Plan parent) { this.parent = parent; }

    public List<ResourceAllocation> getAllocations() { return allocations; }

    public List<Suspension> getSuspensions() { return suspensions; }

    public ImplementedAction getImplementedAction() { return implementedAction; }

    public boolean isSuspended() {
        return suspensions.stream().anyMatch(Suspension::isActive);
    }

    @Override
    public ActionStateEnum getStatus() { return state; }

    @Override
    public BigDecimal getTotalAllocatedQuantity(ResourceType resourceType) {
        return allocations.stream()
                .filter(a -> a.getResourceType().equals(resourceType))
                .map(ResourceAllocation::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void accept(PlanNodeVisitor visitor) {
        visitor.visitLeaf(this);
    }

    public void setImplementedAction(ImplementedAction implementedAction) {
        this.implementedAction = implementedAction;
    }

}