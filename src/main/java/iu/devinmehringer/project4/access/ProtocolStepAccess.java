package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.ProtocolStepRepository;
import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import org.springframework.stereotype.Service;

@Service
public class ProtocolStepAccess {

    private final ProtocolStepRepository protocolStepRepository;

    public ProtocolStepAccess(ProtocolStepRepository protocolStepRepository) {
        this.protocolStepRepository = protocolStepRepository;
    }

    public ProtocolStep save(ProtocolStep protocolStep) {
        return protocolStepRepository.save(protocolStep);
    }

    public void delete(ProtocolStep protocolStep) {
        protocolStepRepository.delete(protocolStep);
    }
}