package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.EntryRepository;
import iu.devinmehringer.project4.model.ledger.Entry;
import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EntryAccess {

    private final EntryRepository entryRepository;

    public EntryAccess(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public List<Entry> getEntriesForAccount(Account account) {
        return entryRepository.findByAccountId(account.getId());
    }

    public BigDecimal getBalanceForAccount(Account account) {
        return entryRepository.sumAmountByAccountId(account.getId());
    }

    public Entry save(Entry entry) {
        return entryRepository.save(entry);
    }
}