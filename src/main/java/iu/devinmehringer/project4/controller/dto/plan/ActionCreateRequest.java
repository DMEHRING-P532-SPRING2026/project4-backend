package iu.devinmehringer.project4.controller.dto.plan;

public class ActionCreateRequest {

    private String name;
    private String party;
    private String timeRef;
    private String location;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }

    public String getTimeRef() { return timeRef; }
    public void setTimeRef(String timeRef) { this.timeRef = timeRef; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}