package iu.devinmehringer.project4.model.plan;

import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan")
public class Plan implements PlanNode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plan_seq")
    @SequenceGenerator(
            name = "plan_seq",
            sequenceName = "plan_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "source_protocol_id")
    private Protocol sourceProtocol;

    private LocalDate targetStartDate;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Plan parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> childPlans = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProposedAction> childActions = new ArrayList<>();

    public Plan() {}

    @Override
    public Long getId() { return id; }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Protocol getSourceProtocol() { return sourceProtocol; }
    public void setSourceProtocol(Protocol sourceProtocol) {
        this.sourceProtocol = sourceProtocol;
    }

    public LocalDate getTargetStartDate() { return targetStartDate; }
    public void setTargetStartDate(LocalDate targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    public Plan getParent() { return parent; }
    public void setParent(Plan parent) { this.parent = parent; }

    public List<PlanNode> getChildren() {
        List<PlanNode> all = new ArrayList<>();
        all.addAll(childPlans);
        all.addAll(childActions);
        return all;
    }

    public void addChild(PlanNode child) {
        if (child instanceof Plan plan) {
            childPlans.add(plan);
            plan.setParent(this);
        } else if (child instanceof ProposedAction action) {
            childActions.add(action);
            action.setParent(this);
        }
    }

    @Override
    public ActionStateEnum getStatus() {
        List<PlanNode> children = getChildren();
        if (children.isEmpty()) return ActionStateEnum.PROPOSED;

        boolean allCompleted = children.stream()
                .allMatch(c -> c.getStatus() == ActionStateEnum.COMPLETED);
        if (allCompleted) return ActionStateEnum.COMPLETED;

        boolean allAbandoned = children.stream()
                .allMatch(c -> c.getStatus() == ActionStateEnum.ABANDONED);
        if (allAbandoned) return ActionStateEnum.ABANDONED;

        boolean anyInProgress = children.stream()
                .anyMatch(c -> c.getStatus() == ActionStateEnum.IN_PROGRESS
                        || c.getStatus() == ActionStateEnum.COMPLETED);
        if (anyInProgress) return ActionStateEnum.IN_PROGRESS;

        boolean anySuspended = children.stream()
                .anyMatch(c -> c.getStatus() == ActionStateEnum.SUSPENDED);
        if (anySuspended) return ActionStateEnum.SUSPENDED;

        return ActionStateEnum.PROPOSED;
    }

    @Override
    public BigDecimal getTotalAllocatedQuantity(ResourceType resourceType) {
        return getChildren().stream()
                .map(child -> child.getTotalAllocatedQuantity(resourceType))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void accept(PlanNodeVisitor visitor) {
        visitor.visit(this);
        getChildren().forEach(child -> child.accept(visitor));
    }
}