package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.action.ActionResponse;
import iu.devinmehringer.project4.controller.dto.action.ImplementRequest;
import iu.devinmehringer.project4.controller.dto.action.ResourceAllocationRequest;
import iu.devinmehringer.project4.controller.dto.action.SuspendRequest;
import iu.devinmehringer.project4.manager.ActionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/actions")
public class ActionController {

    private final ActionManager actionManager;

    public ActionController(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.getAction(id)));
    }

    @GetMapping("/{id}/allocations")
    public ResponseEntity<List<ActionResponse.AllocationResponse>> getAllocations(
            @PathVariable Long id) {
        return ResponseEntity.ok(actionManager.getAllocations(id));
    }

    @PostMapping("/{id}/implement")
    public ResponseEntity<ActionResponse> implement(
            @PathVariable Long id,
            @RequestBody(required = false) ImplementRequest request) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.implement(id, request)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ActionResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.complete(id)));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<ActionResponse> suspend(
            @PathVariable Long id,
            @RequestBody SuspendRequest request) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.suspend(id, request.getReason())));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<ActionResponse> resume(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.resume(id)));
    }

    @PostMapping("/{id}/abandon")
    public ResponseEntity<ActionResponse> abandon(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.abandon(id)));
    }

    @PostMapping("/{id}/allocations")
    public ResponseEntity<ActionResponse> allocate(
            @PathVariable Long id,
            @RequestBody ResourceAllocationRequest request) {
        return ResponseEntity.status(201)
                .body(ActionResponse.from(actionManager.allocate(id, request)));
    }

    @PostMapping("/{id}/submit-for-approval")
    public ResponseEntity<ActionResponse> submitForApproval(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.submitForApproval(id)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ActionResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.approve(id)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ActionResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.reject(id)));
    }

    @PostMapping("/{id}/reopen")
    public ResponseEntity<ActionResponse> reopen(@PathVariable Long id) {
        return ResponseEntity.ok(
                ActionResponse.from(actionManager.reopen(id)));
    }
}