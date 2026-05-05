package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.log.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {
}