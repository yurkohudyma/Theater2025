package ua.hudyma.Theater2025.controller.Rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.Transaction;
import ua.hudyma.Theater2025.service.TicketService;
import ua.hudyma.Theater2025.service.TransactionService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/liqpay-callback")
@RequiredArgsConstructor
public class LiqpayCallbackRestController {

    private final TransactionService transactionService;
    private final TicketService ticketService;

    @Value("${liqpay_private_key}")
    private String privateKey;

    @PostMapping
    @SneakyThrows
    public void handleCallback(@RequestParam Map<String, String> body) {
        String data = body.get("data");
        String signature = body.get("signature");

        if (verifySignature(data, signature)) {
            String decodedJson = getDecodedJson(data);
            log.info("...........LiqPay payment SUCCESSFULL, callback received");
            log.info(decodedJson);
            //Ticket ticket = ticketService.createNewTicket(null); //todo input user data
            Transaction transaction = new ObjectMapper()
                    .readValue(decodedJson, Transaction.class);
            transactionService.addNewTransaction(transaction);
            log.info("........ tx id = {} has SUCCESSFULLY created", transaction.getId());
        } else {
            log.error("........Wrong signature. Potential spoofing attempt.");
            log.info(getDecodedJson(data));
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

