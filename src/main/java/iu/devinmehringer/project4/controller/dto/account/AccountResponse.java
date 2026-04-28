package iu.devinmehringer.project4.controller.dto.account;

import iu.devinmehringer.project4.model.resource.Account;
import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String name;
    private String kind;
    private BigDecimal balance;

    public static AccountResponse from(Account account, BigDecimal balance) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setName(account.getName());
        response.setKind(account.getAccountKind().name());
        response.setBalance(balance);
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}