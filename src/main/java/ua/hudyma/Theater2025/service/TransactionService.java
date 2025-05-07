package ua.hudyma.Theater2025.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.model.Transaction;
import ua.hudyma.Theater2025.repository.TransactionRepository;

import javax.management.AttributeNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionService {

    public static final String REGEX = "_r(\\d+)_s(\\d+)";
    private final TransactionRepository transactionRepository;

    public Transaction getByOrderId (String orderId){
        return transactionRepository
                .findByOrderId(orderId)
                .orElseThrow();
    }

    public void addNewTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        log.info("... tx no.{} has been saved", transaction.getId());
    }

    public OrderId getClearedOrderId(String localOrderId) throws AttributeNotFoundException {
        var pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(localOrderId);
        if (matcher.find()) {
            return new OrderId(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    localOrderId.replaceAll(REGEX, ""));
        }
        throw new AttributeNotFoundException("ордер кривий, давай ще");
    }

    @Data
    @AllArgsConstructor
    public static class OrderId {
        int row, seat;
        String uudd;
    }
}


