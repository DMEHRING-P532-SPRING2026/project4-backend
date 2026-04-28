package iu.devinmehringer.project4.model.knowledge;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="protocol")
public class Protocol {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "protocol_seq")
    @SequenceGenerator(
            name = "protocol_seq",
            sequenceName = "protocol_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable= false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(
            mappedBy = "protocol",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderColumn(name = "step_order")
    private List<ProtocolStep> protocolSteps = new ArrayList<>();

    public Protocol() {}

    public Protocol(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public List<ProtocolStep> getProtocolSteps() {
        return protocolSteps;
    }

    public void setProtocolSteps(List<ProtocolStep> protocolSteps) {
        this.protocolSteps = protocolSteps;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void removeProtocolStep(ProtocolStep protocolStep) {
        this.protocolSteps.remove(protocolStep);
    }
}
