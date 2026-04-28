package iu.devinmehringer.project4.model.ledger;

import iu.devinmehringer.project4.model.resource.Account;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "entry")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entry_seq")
    @SequenceGenerator(
            name = "entry_seq",
            sequenceName = "entry_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant whenCharged;

    @Column(nullable = false)
    private Instant whenBooked;

    protected Entry() {}

    Entry(Transaction transaction, Account account, BigDecimal amount,
          Instant whenCharged, Instant whenBooked) {
        this.transaction = transaction;
        this.account = account;
        this.amount = amount;
        this.whenCharged = whenCharged;
        this.whenBooked = whenBooked;
    }

    public Long getId() { return id; }
    public Transaction getTransaction() { return transaction; }
    public Account getAccount() { return account; }
    public BigDecimal getAmount() { return amount; }
    public Instant getWhenCharged() { return whenCharged; }
    public Instant getWhenBooked() { return whenBooked; }
}