package iu.devinmehringer.project4.model.resource;

import iu.devinmehringer.project4.model.knowledge.ResourceType;
import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(
            name = "account_seq",
            sequenceName = "account_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountKind accountKind;

    @ManyToOne
    @JoinColumn(name = "resource_type_id")
    private ResourceType resourceType;

    public Account() {}

    public Account(String name, AccountKind accountKind, ResourceType resourceType) {
        this.name = name;
        this.accountKind = accountKind;
        this.resourceType = resourceType;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public AccountKind getAccountKind() { return accountKind; }

    public void setAccountKind(AccountKind accountKind) {
        this.accountKind = accountKind;
    }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}