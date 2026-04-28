package iu.devinmehringer.project4.model.ledger;

import iu.devinmehringer.project4.model.plan.ImplementedAction;
import iu.devinmehringer.project4.model.resource.Account;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(
            name = "transaction_seq",
            sequenceName = "transaction_seq",
            allocationSize = 1
    )
    private Long id;

    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "implemented_action_id")
    private ImplementedAction originatingAction;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries = new ArrayList<>();

    public Transaction() {
        this.createdAt = Instant.now();
    }

    public Entry createEntry(Account account, BigDecimal amount,
                             Instant whenCharged, Instant whenBooked) {
        Entry entry = new Entry(this, account, amount, whenCharged, whenBooked);
        entries.add(entry);
        return entry;
    }

    public boolean isBalanced() {
        return entries.stream()
                .map(Entry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .compareTo(BigDecimal.ZERO) == 0;
    }

    public Long getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() { return createdAt; }

    public ImplementedAction getOriginatingAction() { return originatingAction; }
    public void setOriginatingAction(ImplementedAction originatingAction) {
        this.originatingAction = originatingAction;
    }

    public List<Entry> getEntries() { return entries; }
}