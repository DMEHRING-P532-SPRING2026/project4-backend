package iu.devinmehringer.project4.model.knowledge;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="protocol_step")
public class ProtocolStep {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "protocol_step_seq")
    @SequenceGenerator(
            name = "protocol_step_seq",
            sequenceName = "protocol_step_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "protocol_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Protocol protocol;

    @ManyToOne
    @JoinColumn(name = "sub_protocol_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Protocol referencedProtocol;

    @ManyToMany
    @JoinTable(
            name = "protocol_step_dependencies",
            joinColumns = @JoinColumn(name = "step_id"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProtocolStep> dependsOn = new ArrayList<>();

    public ProtocolStep() {}

    public ProtocolStep(Protocol protocol, Protocol referencedProtocol) {
        this.protocol = protocol;
        this.referencedProtocol = referencedProtocol;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Protocol getProtocol() { return protocol; }

    public void setProtocol(Protocol protocol) { this.protocol = protocol; }

    public Protocol getReferencedProtocol() { return referencedProtocol; }

    public void setReferencedProtocol(Protocol referencedProtocol) {
        this.referencedProtocol = referencedProtocol;
    }

    public List<ProtocolStep> getDependsOn() { return dependsOn; }

    public void setDependsOn(List<ProtocolStep> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public void addDependency(ProtocolStep step) {
        dependsOn.add(step);
    }
}