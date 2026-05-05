package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.TransactionRepository;
import iu.devinmehringer.project4.model.ledger.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class TransactionAccess {

    private final TransactionRepository transactionRepository;

    public TransactionAccess(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        Transaction saved = transactionRepository.save(transaction);
        transactionRepository.flush();
        return saved;
    }

    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }
}