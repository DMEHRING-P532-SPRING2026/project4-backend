package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImplementedActionRepository extends JpaRepository<ImplementedAction, Long> {
        Optional<ImplementedAction> findByProposedAction(ProposedAction action);
}
