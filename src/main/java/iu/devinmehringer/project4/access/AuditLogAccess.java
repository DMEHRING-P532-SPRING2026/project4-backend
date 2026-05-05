package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.AuditLogRepository;
import iu.devinmehringer.project4.model.log.AuditLogEntry;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditLogAccess {

    private final AuditLogRepository auditLogRepository;

    public AuditLogAccess(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogEntry save(AuditLogEntry entry) {
        return auditLogRepository.save(entry);
    }

    public List<AuditLogEntry> getAll() {
        return auditLogRepository.findAll();
    }
}