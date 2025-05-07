package ua.hudyma.Theater2025.controller.Rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.constants.liqpay.LiqPayAction;
import ua.hudyma.Theater2025.exception.RefundFailureException;
import ua.hudyma.Theater2025.model.Seat;
import ua.hudyma.Theater2025.model.Transaction;
import ua.hudyma.Theater2025.payment.LiqPayHelper;
import ua.hudyma.Theater2025.repository.SeatRepository;
import ua.hudyma.Theater2025.repository.TicketRepository;
import ua.hudyma.Theater2025.repository.TransactionRepository;
import ua.hudyma.Theater2025.service.TransactionService;
import ua.hudyma.Theater2025.service.TransactionService.OrderId;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class LiqpayRestController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    @Value("${liqpay_public_key}")
    private String publicKey;
    @Value("${liqpay_private_key}")
    private String privateKey;

    @PostMapping("/liqpay-callback")
    @SneakyThrows
    public String handleCallback(@RequestParam Map<String, String> body) {
        String data = body.get("data");
        String signature = body.get("signature");

        if (verifySignature(data, signature)) {
            String decodedJson = getDecodedJson(data);
            log.info("...........LiqPay payment SUCCESSFULL, callback received");
            log.info(decodedJson);

            Transaction transaction = new ObjectMapper()
                    .readValue(decodedJson, Transaction.class);

            OrderId clearedOrderId = transactionService
                    .getClearedOrderId(transaction.getLocalOrderId());
            var orderId = transaction.getLocalOrderId();

            var ticket = ticketRepository.findByOrderId(orderId).orElseThrow();
            ticket.setTicketStatus(TicketStatus.PAID);
            ticket.setOrderId(clearedOrderId.getUudd());
            ticket.setPurchasedOn(LocalDateTime.now());
            ticket.setValue(Double.parseDouble(transaction.getAmount().toString()));
            int seat = clearedOrderId.getSeat();
            ticket.setSeat(seat);
            int row = clearedOrderId.getRow();
            ticket.setRoww(row);
            var newSeat = Seat
                    .builder()
                    .price(transaction.getAmount().doubleValue())
                    .isOccupied(true)
                    .hall(ticket.getHall())
                    .seatNumber(seat)
                    .rowNumber(row)
                    .build();
            seatRepository.save(newSeat);
            log.info("---------new seat {} fixed", newSeat.getId());
            ticketRepository.save(ticket);
            transaction.setTicket(ticket);
            log.info("---------new ticket {} fixed", ticket.getId());
            transactionService.addNewTransaction(transaction);
            log.info("........ tx id = {} has been SUCCESSFULLY created", transaction.getId());
        } else {
            log.error("........Wrong signature. Potential spoofing attempt.");
            log.info(getDecodedJson(data));
        }
        return "redirect:/user";
    }

    @PostMapping("/refund/{id}")
    public String refundPayment(@PathVariable("id") Long id) throws NoSuchAlgorithmException {
        try {
            var ticket = ticketRepository.findById(id).orElseThrow();
            var transaction = transactionService.getByTicketIdAndAction(ticket.getId());
            var refundJson = LiqPayHelper.refundPayment(
                    transaction.getAmount().toString(),
                    publicKey,
                    transaction.getLocalOrderId()
            );
            MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
            String paymentData = LiqPayHelper.getPaymentData(refundJson);
            String signature = LiqPayHelper.getPaymentSignature(paymentData, privateKey);
            request.put("data", Collections.singletonList(paymentData));
            request.put("signature", Collections.singletonList(signature));
            RestTemplate template = new RestTemplate();

            ticket.setTicketStatus(TicketStatus.REFUNDED);
            ticketRepository.save(ticket);
            var refundTransaction = new Transaction();
            refundTransaction.setAction(LiqPayAction.REFUND);
            refundTransaction.setCreateDate(LocalDateTime.now());
            refundTransaction.setDescription("Повернення квитка");
            refundTransaction.setLocalOrderId(transaction.getLocalOrderId());
            refundTransaction.setTicket(ticket);
            transactionRepository.save(refundTransaction);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
             HttpEntity<MultiValueMap<String, String>> liqRequest = new HttpEntity<>(request, headers);
            return template.postForObject(
                    "https://www.liqpay.ua/api/request",
                    liqRequest,
                    String.class);
        } catch (NoSuchAlgorithmException e) {
            throw new RefundFailureException("------повернення не відбулось");
        }
    }

    private static String getDecodedJson(String data) {
        return new String(
                Base64
                        .getDecoder()
                        .decode(data), StandardCharsets.UTF_8);
    }

    private boolean verifySignature(String data, String signature) {
        try {
            String toSign = privateKey + data + privateKey;

            // Обчислюємо SHA-1 хеш
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1 = md.digest(
                    toSign.getBytes(StandardCharsets.UTF_8));

            // Кодуємо результат в Base64
            String calculatedSignature = Base64
                    .getEncoder()
                    .encodeToString(sha1).trim();
            log.info("calculatedSignature: {}", calculatedSignature);
            log.info("acceptedSignature: {}", signature);

            // Порівнюємо з підписом, наданим LiqPay
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
        }
    }

}

