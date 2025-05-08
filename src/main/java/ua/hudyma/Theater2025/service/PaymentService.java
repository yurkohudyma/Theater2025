package ua.hudyma.Theater2025.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
@Log4j2
public class PaymentService {

    @Value("${liqpay_public_key}")
    private String publicKey;
    @Value("${liqpay_private_key}")
    private String privateKey;
    public String getDecodedJson(String data) {
        return new String(
                Base64
                        .getDecoder()
                        .decode(data), StandardCharsets.UTF_8);
    }

    public boolean verifySignature(String data, String signature) {
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
