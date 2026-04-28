package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.PlanRepository;
import iu.devinmehringer.project4.model.plan.Plan;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlanAccess {

    private final PlanRepository planRepository;

    public PlanAccess(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

    public Optional<Plan> getPlan(Long id) {
        return planRepository.findById(id);
    }

    public List<Plan> getPlans() {
        return planRepository.findAll();
    }
}