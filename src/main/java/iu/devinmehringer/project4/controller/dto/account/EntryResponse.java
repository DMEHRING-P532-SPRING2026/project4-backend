package iu.devinmehringer.project4.controller.dto.account;

import iu.devinmehringer.project4.model.ledger.Entry;
import java.math.BigDecimal;
import java.time.Instant;

public class EntryResponse {

    private Long id;
    private BigDecimal amount;
    private Instant whenCharged;
    private Instant whenBooked;
    private Long transactionId;
    private Long accountId;
    private String accountName;
    private String entryType;
    private String resourceTypeKind;

    public static EntryResponse from(Entry entry) {
        EntryResponse response = new EntryResponse();
        response.setId(entry.getId());
        response.setAmount(entry.getAmount());
        response.setWhenCharged(entry.getWhenCharged());
        response.setWhenBooked(entry.getWhenBooked());
        response.setTransactionId(entry.getTransaction().getId());
        response.setAccountId(entry.getAccount().getId());
        response.setAccountName(entry.getAccount().getName());
        response.setEntryType(entry.getAccount().getAccountKind().name());

        if (entry.getTransaction().getResourceTypeKind() != null) {
            response.setResourceTypeKind(
                    entry.getTransaction().getResourceTypeKind().name());
        } else if (entry.getAccount().getResourceType() != null) {
            response.setResourceTypeKind(
                    entry.getAccount().getResourceType().getKind().name());
        }

        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Instant getWhenCharged() { return whenCharged; }
    public void setWhenCharged(Instant whenCharged) { this.whenCharged = whenCharged; }

    public Instant getWhenBooked() { return whenBooked; }
    public void setWhenBooked(Instant whenBooked) { this.whenBooked = whenBooked; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }

    public String getResourceTypeKind() { return resourceTypeKind; }
    public void setResourceTypeKind(String resourceTypeKind) {
        this.resourceTypeKind = resourceTypeKind;
    }
}