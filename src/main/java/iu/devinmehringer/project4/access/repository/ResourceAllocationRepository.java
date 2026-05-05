package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResourceAllocationRepository
        extends JpaRepository<ResourceAllocation, Long> {

    @Query("SELECT a FROM ResourceAllocation a " +
            "JOIN FETCH a.resourceType rt " +
            "LEFT JOIN FETCH rt.poolAccount " +
            "WHERE a.proposedAction.id = :actionId")
    List<ResourceAllocation> findByProposedActionId(@Param("actionId") Long actionId);
}