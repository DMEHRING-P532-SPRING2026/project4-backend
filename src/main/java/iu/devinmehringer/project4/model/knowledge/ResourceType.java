package iu.devinmehringer.project4.model.knowledge;

import jakarta.persistence.*;
import iu.devinmehringer.project4.model.resource.Account;

@Entity
@Table(name = "resource_type")
public class ResourceType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_type_seq")
    @SequenceGenerator(
            name = "resource_type_seq",
            sequenceName = "resource_type_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceTypeKind kind;

    @Column(nullable = false)
    private String unitOfMeasure;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pool_account_id")
    private Account poolAccount;

    public ResourceType() {}

    public ResourceType(String name, ResourceTypeKind kind, String unitOfMeasure, Account poolAccount) {
        this.name = name;
        this.kind = kind;
        this.unitOfMeasure = unitOfMeasure;
        this.poolAccount = poolAccount;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceTypeKind getKind() {
        return kind;
    }

    public void setKind(ResourceTypeKind kind) {
        this.kind = kind;
    }

    public Account getPoolAccount() {
        return poolAccount;
    }

    public void setPoolAccount(Account poolAccount) {
        this.poolAccount = poolAccount;
    }
}
