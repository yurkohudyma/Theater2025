package ua.hudyma.Theater2025.payment;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Log4j2
public class LiqPayHelper {
    private LiqPayHelper() {
    }



    public static JSONObject preparePayment(String amount, String currency, String publicKey) {
        JSONObject json = new JSONObject();
        json.put("version", "3");
        json.put("public_key", publicKey);
        json.put("action", "pay");
        json.put("amount", amount);
        json.put("currency", currency);
        json.put("description", "Тестовий платіж");
        json.put("order_id", "order123456");
        json.put("sandbox", 1); // Увімкнено тестовий режим
        json.put("server_url", "https://your.site/callback");
        return json;
    }

    public static String getData(JSONObject json) {
        return Base64.getEncoder().encodeToString(
                json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String getSignature(String data, String privateKey) throws NoSuchAlgorithmException {
        log.info("Data: " + data);
        return createSignature(privateKey + data + privateKey);
    }

    private static String createSignature(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }
}
