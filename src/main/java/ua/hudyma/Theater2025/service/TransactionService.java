package ua.hudyma.Theater2025.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.model.Transaction;
import ua.hudyma.Theater2025.repository.TransactionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void addNewTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        log.info("... tx no.{} has been saved", transaction.getId());
    }

    /*public LocalDateTime convertTimeStamp (long timestamp){
        return Instant
                .ofEpochMilli(timestamp)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }*/
}
