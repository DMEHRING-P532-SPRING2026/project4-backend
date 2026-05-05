package iu.devinmehringer.project4.controller.dto.log;

import iu.devinmehringer.project4.model.log.AuditLogEntry;
import java.time.Instant;

public class AuditLogResponse {

    private Long id;
    private String event;
    private Long actionId;
    private String actionName;
    private String assetId;
    private String resourceTypeName;
    private Double durationHours;
    private Instant timestamp;

    public static AuditLogResponse from(AuditLogEntry entry) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(entry.getId());
        response.setEvent(entry.getEvent());
        response.setAssetId(entry.getAssetId());
        response.setResourceTypeName(entry.getResourceTypeName());
        response.setDurationHours(entry.getDurationHours());
        response.setTimestamp(entry.getTimestamp());

        if (entry.getAction() != null) {
            response.setActionId(entry.getAction().getId());
            if (entry.getAction().getProposedAction() != null) {
                response.setActionName(
                        entry.getAction().getProposedAction().getName());
            }
        }

        return response;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getEvent() { return event; }

    public void setEvent(String event) { this.event = event; }

    public Long getActionId() { return actionId; }

    public void setActionId(Long actionId) { this.actionId = actionId; }

    public String getActionName() { return actionName; }

    public void setActionName(String actionName) { this.actionName = actionName; }

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

    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}