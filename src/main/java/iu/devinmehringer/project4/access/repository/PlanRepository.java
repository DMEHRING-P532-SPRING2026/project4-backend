package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.plan.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
