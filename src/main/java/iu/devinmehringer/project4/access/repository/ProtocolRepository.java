package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {
    List<Protocol> findByProtocolStepsContaining(ProtocolStep protocolStep);
}
