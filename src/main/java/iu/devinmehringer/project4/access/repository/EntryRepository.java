package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.ledger.Entry;
import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("SELECT e FROM Entry e WHERE e.account.id = :accountId")
    List<Entry> findByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Entry e WHERE e.account.id = :accountId")
    BigDecimal sumAmountByAccountId(@Param("accountId") Long accountId);
}