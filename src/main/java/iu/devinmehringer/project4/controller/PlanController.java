package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.plan.ActionCreateRequest;
import iu.devinmehringer.project4.controller.dto.plan.PlanCreateRequest;
import iu.devinmehringer.project4.controller.dto.plan.PlanReportResponse;
import iu.devinmehringer.project4.controller.dto.plan.PlanResponse;
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
    public ResponseEntity<PlanResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                PlanResponse.from(planManager.getPlan(id)));
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(
            @RequestBody PlanCreateRequest request) {
        return ResponseEntity.status(201)
                .body(PlanResponse.from(planManager.createPlan(request)));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<List<PlanReportResponse>> report(@PathVariable Long id) {
        return ResponseEntity.ok(planManager.generateReport(id));
    }

    // add a sub-plan
    @PostMapping("/{id}/plans")
    public ResponseEntity<PlanResponse> addSubPlan(
            @PathVariable Long id,
            @RequestBody PlanCreateRequest request) {
        return ResponseEntity.status(201)
                .body(PlanResponse.from(planManager.addSubPlan(id, request)));
    }

    // add a proposed action
    @PostMapping("/{id}/actions")
    public ResponseEntity<PlanResponse> addAction(
            @PathVariable Long id,
            @RequestBody ActionCreateRequest request) {
        return ResponseEntity.status(201)
                .body(PlanResponse.from(planManager.addAction(id, request)));
    }
}
