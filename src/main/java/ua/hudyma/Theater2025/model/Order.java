package ua.hudyma.Theater2025.model;

import lombok.Builder;
import ua.hudyma.Theater2025.constants.liqpay.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record Order(String orderId,
                    LocalDateTime createdOn,
                    SeatBatchRequest requestedSeats,
                    OrderStatus status) implements Serializable {

}
