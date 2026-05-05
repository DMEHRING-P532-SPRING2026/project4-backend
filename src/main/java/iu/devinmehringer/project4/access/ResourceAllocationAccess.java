package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.ResourceAllocationRepository;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResourceAllocationAccess {

    private final ResourceAllocationRepository resourceAllocationRepository;

    public ResourceAllocationAccess(
            ResourceAllocationRepository resourceAllocationRepository) {
        this.resourceAllocationRepository = resourceAllocationRepository;
    }

    public List<ResourceAllocation> findByProposedActionId(Long actionId) {
        return resourceAllocationRepository.findByProposedActionId(actionId);
    }
}