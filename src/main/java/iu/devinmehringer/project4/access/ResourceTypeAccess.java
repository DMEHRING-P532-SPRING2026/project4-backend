package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.ResourceTypeRepository;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceTypeAccess {
    private final ResourceTypeRepository resourceTypeRepository;

    public ResourceTypeAccess(ResourceTypeRepository resourceTypeRepository) {
        this.resourceTypeRepository = resourceTypeRepository;
    }

    public ResourceType save(ResourceType resourceType) {
        return this.resourceTypeRepository.save(resourceType);
    }

    public List<ResourceType> getResourceTypes() {
        return this.resourceTypeRepository.findAll();
    }

    public Optional<ResourceType> getResourceType(Long id) {
        return this.resourceTypeRepository.findById(id);
    }

    public void deleteResourceType(ResourceType resourceType) {
        this.resourceTypeRepository.delete(resourceType);
    }

}
