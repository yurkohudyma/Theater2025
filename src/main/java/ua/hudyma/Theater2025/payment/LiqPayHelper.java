package ua.hudyma.Theater2025.payment;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Log4j2
public class LiqPayHelper {
    private static final String CURRENCY = "UAH";
    public static final int VERSION = 3;


    private LiqPayHelper() {
    }

    public static JSONObject preparePayment(String amount,
                                            String publicKey,
                                            String paymentDescription,
                                            String orderId,
                                            String serverUrl) {
        JSONObject json = new JSONObject();
        json.put("version", VERSION);
        json.put("public_key", publicKey);
        json.put("action", "pay");
        json.put("amount", amount);
        json.put("currency", CURRENCY);
        json.put("description", paymentDescription);
        json.put("order_id", orderId);
        json.put("sandbox", 1); // Увімкнено тестовий режим
        json.put("server_url", serverUrl + "/liqpay-callback");
        return json;
    }

    public static JSONObject refundPayment (String amount,
                                            String publicKey,
                                            String orderId){
        JSONObject json = new JSONObject();
        json.put("version", VERSION);
        json.put("action", "refund");
        json.put("public_key", publicKey);
        json.put("order_id", orderId);
        json.put("amount", amount);
        json.put("currency", CURRENCY);

        return json;
    }

    public static String getPaymentData(JSONObject json) {
        return Base64.getEncoder().encodeToString(
                json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String getPaymentSignature(String data, String privateKey) throws NoSuchAlgorithmException {
        log.info("Data: " + data);
        return createSignature(privateKey + data + privateKey);
    }

    private static String createSignature(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }
}
