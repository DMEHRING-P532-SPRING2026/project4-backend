package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.ledger.Entry;
import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByAccount(Account account);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Entry e WHERE e.account = :account")
    BigDecimal sumAmountByAccount(@Param("account") Account account);
}