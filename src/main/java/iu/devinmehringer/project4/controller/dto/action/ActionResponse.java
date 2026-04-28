package iu.devinmehringer.project4.controller.dto.action;

import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActionResponse {

    private Long id;
    private String name;
    private ActionStateEnum state;

    // planned values
    private String plannedParty;
    private String plannedTimeRef;
    private String plannedLocation;

    // actual values — null if not yet implemented
    private Instant actualStart;
    private String actualParty;
    private String actualLocation;

    // differences
    private boolean partyDiffers;
    private boolean locationDiffers;

    private List<AllocationResponse> allocations = new ArrayList<>();

    public static class AllocationResponse {
        private Long id;
        private Long resourceTypeId;
        private String resourceTypeName;
        private BigDecimal quantity;
        private String kind;
        private String assetId;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getResourceTypeId() { return resourceTypeId; }
        public void setResourceTypeId(Long resourceTypeId) {
            this.resourceTypeId = resourceTypeId;
        }

        public String getResourceTypeName() { return resourceTypeName; }
        public void setResourceTypeName(String resourceTypeName) {
            this.resourceTypeName = resourceTypeName;
        }

        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

        public String getKind() { return kind; }
        public void setKind(String kind) { this.kind = kind; }

        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }
    }

    public static ActionResponse from(ProposedAction action) {
        ActionResponse response = new ActionResponse();
        response.setId(action.getId());
        response.setName(action.getName());
        response.setState(action.getState());
        response.setPlannedParty(action.getParty());
        response.setPlannedTimeRef(action.getTimeRef());
        response.setPlannedLocation(action.getLocation());

        ImplementedAction implemented = action.getImplementedAction();
        if (implemented != null) {
            response.setActualStart(implemented.getActualStart());
            response.setActualParty(implemented.getActualParty());
            response.setActualLocation(implemented.getActualLocation());
            response.setPartyDiffers(
                    equals(action.getParty(), implemented.getActualParty()));
            response.setLocationDiffers(
                    equals(action.getLocation(), implemented.getActualLocation()));
        }

        for (ResourceAllocation allocation : action.getAllocations()) {
            AllocationResponse alloc = new AllocationResponse();
            alloc.setId(allocation.getId());
            alloc.setResourceTypeId(allocation.getResourceType().getId());
            alloc.setResourceTypeName(allocation.getResourceType().getName());
            alloc.setQuantity(allocation.getQuantity());
            alloc.setKind(allocation.getKind().name());
            alloc.setAssetId(allocation.getAssetId());
            response.getAllocations().add(alloc);
        }

        return response;
    }

    private static boolean equals(String a, String b) {
        if (a == null && b == null) return false;
        if (a == null || b == null) return true;
        return !a.equals(b);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ActionStateEnum getState() { return state; }
    public void setState(ActionStateEnum state) { this.state = state; }

    public String getPlannedParty() { return plannedParty; }
    public void setPlannedParty(String plannedParty) { this.plannedParty = plannedParty; }

    public String getPlannedTimeRef() { return plannedTimeRef; }
    public void setPlannedTimeRef(String plannedTimeRef) {
        this.plannedTimeRef = plannedTimeRef;
    }

    public String getPlannedLocation() { return plannedLocation; }
    public void setPlannedLocation(String plannedLocation) {
        this.plannedLocation = plannedLocation;
    }

    public Instant getActualStart() { return actualStart; }
    public void setActualStart(Instant actualStart) { this.actualStart = actualStart; }

    public String getActualParty() { return actualParty; }
    public void setActualParty(String actualParty) { this.actualParty = actualParty; }

    public String getActualLocation() { return actualLocation; }
    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public boolean isPartyDiffers() { return partyDiffers; }
    public void setPartyDiffers(boolean partyDiffers) { this.partyDiffers = partyDiffers; }

    public boolean isLocationDiffers() { return locationDiffers; }
    public void setLocationDiffers(boolean locationDiffers) {
        this.locationDiffers = locationDiffers;
    }

    public List<AllocationResponse> getAllocations() { return allocations; }
    public void setAllocations(List<AllocationResponse> allocations) {
        this.allocations = allocations;
    }
}