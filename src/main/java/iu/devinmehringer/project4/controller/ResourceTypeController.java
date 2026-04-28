package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.resource.ResourceTypeRequest;
import iu.devinmehringer.project4.controller.dto.resource.ResourceTypeResponse;
import iu.devinmehringer.project4.manager.KnowledgeManager;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource-types")
public class ResourceTypeController {

    private final KnowledgeManager knowledgeManager;

    public ResourceTypeController(KnowledgeManager knowledgeManager) {
        this.knowledgeManager = knowledgeManager;
    }

    @GetMapping
    public List<ResourceTypeResponse> list() {
        return knowledgeManager.getResourceTypes().stream()
                .map(ResourceTypeResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceTypeResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResourceTypeResponse.from(knowledgeManager.getResourceType(id))
        );
    }

    @PostMapping
    public ResponseEntity<ResourceTypeResponse> create(
            @RequestBody ResourceTypeRequest request) {
        return ResponseEntity.status(201)
                .body(ResourceTypeResponse.from(
                        knowledgeManager.createResourceType(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceTypeResponse> update(
            @PathVariable Long id,
            @RequestBody ResourceTypeRequest request) {
        return ResponseEntity.ok(
                ResourceTypeResponse.from(
                        knowledgeManager.updateResourceType(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        knowledgeManager.deleteResourceType(id);
        return ResponseEntity.ok().build();
    }
}