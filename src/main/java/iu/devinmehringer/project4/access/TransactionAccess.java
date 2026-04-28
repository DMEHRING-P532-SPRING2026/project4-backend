package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.TransactionRepository;
import iu.devinmehringer.project4.model.ledger.Transaction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TransactionAccess {

    private final TransactionRepository transactionRepository;

    public TransactionAccess(@Lazy TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }
}