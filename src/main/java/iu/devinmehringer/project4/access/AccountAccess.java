package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.AccountRepository;
import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccountAccess {

    private final AccountRepository accountRepository;

    public AccountAccess(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccount(Long id) {
        return accountRepository.findById(id);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}