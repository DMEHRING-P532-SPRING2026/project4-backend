package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.knowledge.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, Long> {
}
