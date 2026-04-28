package iu.devinmehringer.project4.controller.dto.plan;

import java.time.LocalDate;

public class PlanCreateRequest {

    private String name;
    private Long sourceProtocolId;    // optional — if set, create from protocol
    private LocalDate targetStartDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getSourceProtocolId() { return sourceProtocolId; }
    public void setSourceProtocolId(Long sourceProtocolId) {
        this.sourceProtocolId = sourceProtocolId;
    }

    public LocalDate getTargetStartDate() { return targetStartDate; }
    public void setTargetStartDate(LocalDate targetStartDate) {
        this.targetStartDate = targetStartDate;
    }
}