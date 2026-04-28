package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.plan.ProposedAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposedActionRepository extends JpaRepository<ProposedAction, Long> {

}
