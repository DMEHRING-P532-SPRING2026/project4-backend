package iu.devinmehringer.project4.model.log;

import iu.devinmehringer.project4.model.plan.ImplementedAction;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_log_entry")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_seq")
    @SequenceGenerator(
            name = "audit_log_seq",
            sequenceName = "audit_log_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String event;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ImplementedAction action;

    private String assetId;

    private String resourceTypeName;

    private Double durationHours;

    @Column(nullable = false)
    private Instant timestamp;

    public AuditLogEntry() {
        this.timestamp = Instant.now();
    }

    public Long getId() { return id; }

    public String getEvent() { return event; }

    public void setEvent(String event) { this.event = event; }

    public ImplementedAction getAction() { return action; }

    public void setAction(ImplementedAction action) { this.action = action; }

    public String getAssetId() { return assetId; }

    public void setAssetId(String assetId) { this.assetId = assetId; }

    public String getResourceTypeName() { return resourceTypeName; }

    public void setResourceTypeName(String resourceTypeName) {
        this.resourceTypeName = resourceTypeName;
    }

    public Double getDurationHours() { return durationHours; }

    public void setDurationHours(Double durationHours) {
        this.durationHours = durationHours;
    }

    public Instant getTimestamp() { return timestamp; }
}