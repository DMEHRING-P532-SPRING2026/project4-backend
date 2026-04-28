package iu.devinmehringer.project4.controller.dto.protocol;

import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProtocolResponse {

    private Long id;
    private String name;
    private String description;
    private List<ProtocolStepResponse> steps = new ArrayList<>();

    public static class ProtocolStepResponse {
        private Long id;
        private ProtocolResponse subProtocol;
        private List<Integer> dependsOn = new ArrayList<>();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public ProtocolResponse getSubProtocol() { return subProtocol; }
        public void setSubProtocol(ProtocolResponse subProtocol) {
            this.subProtocol = subProtocol;
        }

        public List<Integer> getDependsOn() { return dependsOn; }
        public void setDependsOn(List<Integer> dependsOn) {
            this.dependsOn = dependsOn;
        }
    }

    public static ProtocolResponse from(Protocol protocol) {
        ProtocolResponse response = new ProtocolResponse();
        response.setId(protocol.getId());
        response.setName(protocol.getName());
        response.setDescription(protocol.getDescription());

        List<ProtocolStep> steps = protocol.getProtocolSteps()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        for (int i = 0; i < steps.size(); i++) {
            ProtocolStep step = steps.get(i);
            ProtocolStepResponse stepResponse = new ProtocolStepResponse();
            stepResponse.setId(step.getId());
            stepResponse.setSubProtocol(from(step.getReferencedProtocol()));
            stepResponse.setDependsOn(buildDependsOn(step, steps));
            response.getSteps().add(stepResponse);
        }

        return response;
    }

    private static List<Integer> buildDependsOn(
            ProtocolStep step,
            List<ProtocolStep> steps) {

        List<Integer> dependsOn = new ArrayList<>();
        for (ProtocolStep dep : step.getDependsOn()) {
            for (int j = 0; j < steps.size(); j++) {
                if (steps.get(j).getId().equals(dep.getId())) {
                    dependsOn.add(j);
                    break;
                }
            }
        }
        return dependsOn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProtocolStepResponse> getSteps() { return steps; }
    public void setSteps(List<ProtocolStepResponse> steps) {
        this.steps = steps;
    }
}