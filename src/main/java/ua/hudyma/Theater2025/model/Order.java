package ua.hudyma.Theater2025.model;

import lombok.Builder;
import ua.hudyma.Theater2025.constants.liqpay.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Order(UUID orderId,
                    LocalDateTime createdOn,
                    SeatBatchRequest requestedSeats,
                    OrderStatus status) {

}
