package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.plan.*;
import iu.devinmehringer.project4.manager.PlanManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanManager planManager;

    public PlanController(PlanManager planManager) {
        this.planManager = planManager;
    }

    @GetMapping
    public List<PlanResponse> list() {
        return planManager.getPlans().stream()
                .map(PlanResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> get(
            @PathVariable Long id,
            @RequestParam(required = false) Integer depth) {
        return ResponseEntity.ok(planManager.getPlanResponse(id, depth));
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(
            @RequestBody PlanCreateRequest request) {
        return ResponseEntity.status(201)
                .body(PlanResponse.from(planManager.createPlan(request)));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<List<PlanReportResponse>> report(
            @PathVariable Long id,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(planManager.generateReport(id, status));
    }

    @PostMapping("/{id}/plans")
    public ResponseEntity<PlanResponse> addSubPlan(
            @PathVariable Long id,
            @RequestBody PlanCreateRequest request) {
        return ResponseEntity.status(201)
                .body(PlanResponse.from(planManager.addSubPlan(id, request)));
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<PlanResponse> addAction(
            @PathVariable Long id,
            @RequestBody ActionCreateRequest request) {
        return ResponseEntity.status(201)
                .body(PlanResponse.from(planManager.addAction(id, request)));
    }

    @GetMapping("/{planId}/metrics")
    public ResponseEntity<PlanMetricsResponse> metrics(
            @PathVariable Long planId,
            @RequestParam(required = false) Long nodeId) {
        Long targetId = nodeId != null ? nodeId : planId;
        return ResponseEntity.ok(planManager.getMetrics(planId, targetId));
    }
}