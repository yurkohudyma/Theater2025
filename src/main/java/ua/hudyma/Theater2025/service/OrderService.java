package ua.hudyma.Theater2025.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.hudyma.Theater2025.model.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    public static final String ORDER_SER_URL = "res//order.ser";

    public String storeOrderInMemoryMap(Order order) {
        String orderId = order.orderId();
        orders.put(orderId, order);
        return orderId;
    }

    public Optional<Order> retrieveOrderFromInMemoryMap(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public void remove(String orderId) {
        orders.remove(orderId);
    }



    public void serializeOrder(Order order) {
        try (var oos = new ObjectOutputStream(new FileOutputStream(ORDER_SER_URL))) {
            oos.writeObject(order);
        } catch (IOException e) {
            e.printStackTrace(); // покаже точну причину
        }
    }
    public Order deserializeOrder (){
        try (var oos = new ObjectInputStream(new FileInputStream(ORDER_SER_URL))){
            return (Order) oos.readObject();
        } catch (ClassNotFoundException | IOException e) {
            //panic!!!
        }
        return null;
    }

    public void saveOrderToCookie(HttpServletResponse response,
                                  Order order) throws IOException {
        String json = objectMapper.writeValueAsString(order);
        String encoded = Base64
                .getEncoder()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        Cookie cookie = new Cookie("order", encoded);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60); // 1 година
        response.addCookie(cookie);
    }

    public Optional<Order> readOrderFromCookie(String cookie) {
        try {
            String decoded = new String(Base64.getDecoder()
                    .decode(cookie), StandardCharsets.UTF_8);
            Order order = objectMapper
                    .readValue(decoded, Order.class);
            return Optional.of(order);
        } catch (Exception e) {
            // handle error
        }
        return Optional.empty();
    }


}
