package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.protocol.ProtocolCreateRequest;
import iu.devinmehringer.project4.controller.dto.protocol.ProtocolResponse;
import iu.devinmehringer.project4.manager.KnowledgeManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/protocols")
public class ProtocolController {

    private final KnowledgeManager knowledgeManager;

    public ProtocolController(KnowledgeManager knowledgeManager) {
        this.knowledgeManager = knowledgeManager;
    }

    @GetMapping
    public List<ProtocolResponse> list() {
        return knowledgeManager.getProtocols().stream()
                .map(ProtocolResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocolResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                ProtocolResponse.from(knowledgeManager.getProtocol(id))
        );
    }

    @PostMapping
    public ResponseEntity<ProtocolResponse> create(
            @RequestBody ProtocolCreateRequest request) {
        return ResponseEntity.status(201)
                .body(ProtocolResponse.from(knowledgeManager.createProtocol(request)));
    }
}