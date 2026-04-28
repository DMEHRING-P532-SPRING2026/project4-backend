package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.ProposedActionRepository;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProposedActionAccess {
    private ProposedActionRepository proposedActionRepository;

    public ProposedActionAccess(ProposedActionRepository proposedActionRepository) {
        this.proposedActionRepository = proposedActionRepository;
    }

    public ProposedAction save(ProposedAction proposedAction) {
        return this.proposedActionRepository.save(proposedAction);
    }
    
    public Optional<ProposedAction> getProposedAction(Long id) {
        return this.proposedActionRepository.findById(id);
    }
}
