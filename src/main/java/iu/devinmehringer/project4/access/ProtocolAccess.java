package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.ProtocolRepository;
import iu.devinmehringer.project4.controller.exception.ProtocolNotFoundException;
import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProtocolAccess {
    private final ProtocolRepository protocolRepository;

    public ProtocolAccess(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    public List<Protocol> getProtocols() {
        return protocolRepository.findAll();
    }

    public Optional<Protocol> getProtocol(Long id) {
        return protocolRepository.findById(id);
    }

    public Protocol save(Protocol protocol) {
        return protocolRepository.save(protocol);
    }

    public void deleteProtocol(Protocol protocol) {
        protocolRepository.delete(protocol);
    }

    public void deleteProtocolById(Long id) {
        protocolRepository.delete(this.getProtocol(id).orElseThrow(() -> new ProtocolNotFoundException(id.toString())));
    }

    public List<Protocol> findByStep(ProtocolStep step) {
        return protocolRepository.findByProtocolStepsContaining(step);
    }

    public void flush() {
        protocolRepository.flush();
    }
}
