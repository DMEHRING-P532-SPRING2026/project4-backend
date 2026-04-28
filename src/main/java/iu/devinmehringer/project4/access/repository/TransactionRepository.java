package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.ledger.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
