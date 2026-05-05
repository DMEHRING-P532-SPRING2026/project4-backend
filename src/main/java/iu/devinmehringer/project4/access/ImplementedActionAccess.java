package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.ImplementedActionRepository;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImplementedActionAccess {
    private final ImplementedActionRepository implementedActionRepository;

    public ImplementedActionAccess(ImplementedActionRepository implementedActionRepository) {
        this.implementedActionRepository = implementedActionRepository;
    }

    public void save(ImplementedAction implementedAction) {
        this.implementedActionRepository.save(implementedAction);
    }

    public Optional<ImplementedAction> findByProposedAction(ProposedAction action) {
        return implementedActionRepository.findByProposedAction(action);
    }
}
