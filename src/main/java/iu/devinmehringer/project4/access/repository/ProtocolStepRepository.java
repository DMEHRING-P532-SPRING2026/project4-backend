package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProtocolStepRepository extends JpaRepository<ProtocolStep, Long> {
    void deleteByReferencedProtocol(Protocol protocol);
}
