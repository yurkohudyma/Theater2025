package ua.hudyma.Theater2025.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.hudyma.Theater2025.constants.liqpay.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long paymentId;
    @Enumerated(EnumType.STRING)
    LiqPayAction action;
    @Enumerated(EnumType.STRING)
    LiqPayStatus status;
    @Enumerated(EnumType.STRING)
    LiqPayType type;
    LiqPaySystemPaymentType systemPaymentType;
    String payType;
    Integer acqBankId;
    String localOrderId;
    String liqpayOrderId;
    String description;
    Long senderPhone;
    String senderCardMask;
    String senderBank;
    @Enumerated(EnumType.STRING)
    LiqpayCardType senderCardType;
    Integer senderCardCountry;
    BigDecimal amount;
    String currency;
    BigDecimal liqpayCommission;
    LocalDateTime createDate;
    LocalDateTime endDate;

}
