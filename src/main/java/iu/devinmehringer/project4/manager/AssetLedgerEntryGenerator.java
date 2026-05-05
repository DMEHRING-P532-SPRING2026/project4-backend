package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.access.AuditLogAccess;
import iu.devinmehringer.project4.access.EntryAccess;
import iu.devinmehringer.project4.access.ResourceAllocationAccess;
import iu.devinmehringer.project4.manager.engine.OverConsumptionPostingRule;
import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;
import iu.devinmehringer.project4.model.ledger.Entry;
import iu.devinmehringer.project4.model.ledger.Transaction;
import iu.devinmehringer.project4.model.log.AuditLogEntry;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import iu.devinmehringer.project4.model.resource.ResourceAllocationKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class AssetLedgerEntryGenerator extends AbstractLedgerEntryGenerator {

    private static final Logger log =
            LoggerFactory.getLogger(AssetLedgerEntryGenerator.class);

    private final AuditLogAccess auditLogAccess;
    private final ResourceAllocationAccess resourceAllocationAccess;

    public AssetLedgerEntryGenerator(
            AuditLogAccess auditLogAccess,
            OverConsumptionPostingRule postingRuleEngine,
            EntryAccess entryAccess,
            ResourceAllocationAccess resourceAllocationAccess) {
        this.auditLogAccess = auditLogAccess;
        this.resourceAllocationAccess = resourceAllocationAccess;
        setPostingRuleEngine(postingRuleEngine);
        setEntryAccess(entryAccess);
    }

    @Override
    protected List<ResourceAllocation> selectAllocations(ImplementedAction action) {
        Long proposedActionId = action.getProposedAction() != null
                ? action.getProposedAction().getId() : null;

        log.info("AssetGenerator selectAllocations: implementedActionId={} proposedActionId={}",
                action.getId(), proposedActionId);

        List<ResourceAllocation> all;
        if (proposedActionId != null) {
            all = resourceAllocationAccess.findByProposedActionId(proposedActionId);
        } else {
            // fallback for unit tests — no DB, use in-memory allocations
            all = action.getProposedAction() != null
                    ? action.getProposedAction().getAllocations()
                    : List.of();
        }

        log.info("Total allocations found: {}", all.size());
        all.forEach(a -> log.info("  id={} kind={} rtKind={} assetId={}",
                a.getId(), a.getKind(),
                a.getResourceType().getKind(), a.getAssetId()));

        List<ResourceAllocation> filtered = all.stream()
                .filter(a -> a.getResourceType().getKind() == ResourceTypeKind.ASSET)
                .filter(a -> a.getKind() == ResourceAllocationKind.SPECIFIC)
                .toList();

        log.info("Filtered ASSET SPECIFIC allocations: {}", filtered.size());
        return filtered;
    }

    @Override
    protected void validate(List<ResourceAllocation> allocs) {
        for (ResourceAllocation a : allocs) {
            if (a.getTimePeriodStart() == null || a.getTimePeriodEnd() == null) {
                throw new IllegalStateException(
                        "Asset allocation for '" + a.getResourceType().getName()
                                + "' must have a time period");
            }
            double hours = Duration.between(
                    a.getTimePeriodStart(), a.getTimePeriodEnd()).toHours();
            if (hours <= 0) {
                throw new IllegalStateException(
                        "Asset allocation for '" + a.getResourceType().getName()
                                + "' must have a positive duration, got: " + hours);
            }
        }
    }

    @Override
    protected Entry buildWithdrawal(Transaction tx, ResourceAllocation a) {
        double hours = Duration.between(
                a.getTimePeriodStart(), a.getTimePeriodEnd()).toHours();
        Instant now = Instant.now();
        return tx.createEntry(
                a.getResourceType().getPoolAccount(),
                BigDecimal.valueOf(hours).negate(),
                tx.getOriginatingAction().getActualStart(),
                now
        );
    }

    @Override
    protected Entry buildDeposit(Transaction tx, ResourceAllocation a) {
        double hours = Duration.between(
                a.getTimePeriodStart(), a.getTimePeriodEnd()).toHours();
        Instant now = Instant.now();
        return tx.createEntry(
                tx.getOriginatingAction().getUsageAccount(),
                BigDecimal.valueOf(hours),
                tx.getOriginatingAction().getActualStart(),
                now
        );
    }

    @Override
    protected void afterPost(Transaction tx) {
        ImplementedAction action = tx.getOriginatingAction();
        Long proposedActionId = action.getProposedAction() != null
                ? action.getProposedAction().getId() : null;

        log.info("afterPost: implementedActionId={} proposedActionId={}",
                action.getId(), proposedActionId);

        List<ResourceAllocation> source;
        if (proposedActionId != null) {
            source = resourceAllocationAccess.findByProposedActionId(proposedActionId);
        } else {
            source = action.getProposedAction() != null
                    ? action.getProposedAction().getAllocations()
                    : List.of();
        }

        List<ResourceAllocation> allocs = source.stream()
                .filter(a -> a.getResourceType().getKind() == ResourceTypeKind.ASSET)
                .filter(a -> a.getKind() == ResourceAllocationKind.SPECIFIC)
                .filter(a -> a.getTimePeriodStart() != null
                        && a.getTimePeriodEnd() != null)
                .toList();

        log.info("afterPost: writing {} audit log entries", allocs.size());

        allocs.forEach(a -> {
            double hours = Duration.between(
                    a.getTimePeriodStart(), a.getTimePeriodEnd()).toHours();
            AuditLogEntry auditLog = new AuditLogEntry();
            auditLog.setEvent("ASSET_UTILISATION");
            auditLog.setAction(action);
            auditLog.setAssetId(a.getAssetId());
            auditLog.setResourceTypeName(a.getResourceType().getName());
            auditLog.setDurationHours(hours);
            auditLogAccess.save(auditLog);
            log.info("afterPost: audit log entry saved for asset={}", a.getAssetId());
        });
    }
}