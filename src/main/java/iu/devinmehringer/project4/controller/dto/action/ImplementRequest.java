package iu.devinmehringer.project4.controller.dto.action;

public class ImplementRequest {

    private String actualParty;
    private String actualLocation;

    public String getActualParty() { return actualParty; }
    public void setActualParty(String actualParty) { this.actualParty = actualParty; }

    public String getActualLocation() { return actualLocation; }
    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }
}