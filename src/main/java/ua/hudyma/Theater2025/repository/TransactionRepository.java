package ua.hudyma.Theater2025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Transactional
    List<Transaction> findAll();
}
