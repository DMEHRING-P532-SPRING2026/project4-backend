package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.access.AccountAccess;
import iu.devinmehringer.project4.access.PostingRuleAccess;
import iu.devinmehringer.project4.access.ProtocolAccess;
import iu.devinmehringer.project4.access.ResourceTypeAccess;
import iu.devinmehringer.project4.controller.dto.protocol.ProtocolCreateRequest;
import iu.devinmehringer.project4.controller.dto.resource.ResourceTypeRequest;
import iu.devinmehringer.project4.controller.exception.ProtocolNotFoundException;
import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.ledger.PostingRule;
import iu.devinmehringer.project4.model.ledger.PostingRuleStrategyType;
import iu.devinmehringer.project4.model.resource.Account;
import iu.devinmehringer.project4.model.resource.AccountKind;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class KnowledgeManager {

    private final ProtocolAccess protocolAccess;
    private final ResourceTypeAccess resourceTypeAccess;
    private final AccountAccess accountAccess;
    private final PostingRuleAccess postingRuleAccess;

    public KnowledgeManager(
            ProtocolAccess protocolAccess,
            ResourceTypeAccess resourceTypeAccess,
            AccountAccess accountAccess,
            PostingRuleAccess postingRuleAccess) {
        this.protocolAccess = protocolAccess;
        this.resourceTypeAccess = resourceTypeAccess;
        this.accountAccess = accountAccess;
        this.postingRuleAccess = postingRuleAccess;
    }

    public List<Protocol> getProtocols() {
        return protocolAccess.getProtocols();
    }

    public Protocol getProtocol(Long id) {
        return protocolAccess.getProtocol(id)
                .orElseThrow(() -> new ProtocolNotFoundException(id.toString()));
    }

    public Protocol createProtocol(ProtocolCreateRequest request) {
        if (request.getId() == null && request.getName() == null) {
            throw new IllegalArgumentException("Protocol must have a name or an id");
        }
        Protocol protocol = new Protocol();
        protocol.setName(request.getName());
        protocol.setDescription(request.getDescription());
        return buildProtocol(protocol, request);
    }

    private Protocol buildProtocol(Protocol protocol, ProtocolCreateRequest request) {
        List<ProtocolStep> steps = new ArrayList<>();

        for (ProtocolCreateRequest stepRequest : request.getSteps()) {
            Protocol subProtocol = createOrReferenceProtocol(stepRequest);
            ProtocolStep step = new ProtocolStep();
            step.setProtocol(protocol);
            step.setReferencedProtocol(subProtocol);
            steps.add(step);
        }

        for (int i = 0; i < request.getSteps().size(); i++) {
            ProtocolCreateRequest stepRequest = request.getSteps().get(i);
            ProtocolStep step = steps.get(i);

            for (Integer dependsOnIndex : stepRequest.getDependsOn()) {
                if (dependsOnIndex < 0 || dependsOnIndex >= i) {
                    throw new IllegalArgumentException(
                            "Step " + i + " has invalid dependency index: "
                                    + dependsOnIndex);
                }
                step.addDependency(steps.get(dependsOnIndex));
            }
        }

        protocol.getProtocolSteps().addAll(steps);
        return protocolAccess.save(protocol);
    }

    private Protocol createOrReferenceProtocol(ProtocolCreateRequest request) {
        if (request.getId() != null) {
            return protocolAccess.getProtocol(request.getId())
                    .orElseThrow(() -> new ProtocolNotFoundException(
                            request.getId().toString()));
        }
        return createProtocol(request);
    }

    public List<ResourceType> getResourceTypes() {
        return resourceTypeAccess.getResourceTypes();
    }

    public ResourceType getResourceType(Long id) {
        return resourceTypeAccess.getResourceType(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ResourceType not found: " + id));
    }

    public ResourceType createResourceType(ResourceTypeRequest request) {
        Account poolAccount = new Account();
        poolAccount.setName(request.getName() + " Pool");
        poolAccount.setAccountKind(AccountKind.POOL);

        Account alertAccount = new Account();
        alertAccount.setName(request.getName() + " Alert");
        alertAccount.setAccountKind(AccountKind.ALERT_MEMO);

        ResourceType resourceType = new ResourceType();
        resourceType.setName(request.getName());
        resourceType.setKind(request.getKind());
        resourceType.setUnitOfMeasure(request.getUnitOfMeasure());
        resourceType.setUnitCost(request.getUnitCost() != null
                ? request.getUnitCost() : BigDecimal.ZERO);
        resourceType.setPoolAccount(poolAccount);

        poolAccount.setResourceType(resourceType);
        alertAccount.setResourceType(resourceType);

        resourceTypeAccess.save(resourceType);
        accountAccess.save(alertAccount);

        PostingRule rule = new PostingRule();
        rule.setTriggerAccount(poolAccount);
        rule.setOutputAccount(alertAccount);
        rule.setStrategyType(PostingRuleStrategyType.OVER_CONSUMPTION_ALERT);
        postingRuleAccess.save(rule);

        return resourceType;
    }

    public ResourceType updateResourceType(Long id, ResourceTypeRequest request) {
        ResourceType existing = getResourceType(id);

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getKind() != null) existing.setKind(request.getKind());
        if (request.getUnitOfMeasure() != null) {
            existing.setUnitOfMeasure(request.getUnitOfMeasure());
        }
        if (request.getUnitCost() != null) {
            existing.setUnitCost(request.getUnitCost());
        }

        return resourceTypeAccess.save(existing);
    }

    public void deleteResourceType(Long id) {
        ResourceType resourceType = resourceTypeAccess.getResourceType(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ResourceType not found: " + id));
        resourceTypeAccess.deleteResourceType(resourceType);
    }
}