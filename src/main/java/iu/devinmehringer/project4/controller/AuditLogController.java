package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.log.AuditLogResponse;
import iu.devinmehringer.project4.manager.ActionManager;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {

    private final ActionManager actionManager;

    public AuditLogController(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @GetMapping
    public List<AuditLogResponse> getAuditLog() {
        return actionManager.getAuditLog().stream()
                .map(AuditLogResponse::from)
                .toList();
    }
}