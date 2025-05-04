package ua.hudyma.Theater2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.hudyma.Theater2025.constants.liqpay.*;
import util.UnixToLocalDateTimeDeserializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @JsonProperty("payment_id")
    Long paymentId;
    @Enumerated(EnumType.STRING)
    LiqPayAction action;
    @Enumerated(EnumType.STRING)
    LiqPayStatus status;
    @Enumerated(EnumType.STRING)
    LiqPayType type;
    @JsonProperty("paytype")
    @Enumerated(EnumType.STRING)
    LiqPaySystemPaymentType systemPaymentType;
    @JsonProperty("acq_id")
    Integer acqBankId;
    @JsonProperty("order_id")
    String localOrderId;
    @JsonProperty("liqpay_order_id")
    String liqpayOrderId;
    String description;
    @JsonProperty("sender_phone")
    Long senderPhone;
    @JsonProperty("sender_card_mask2")
    String senderCardMask;
    @JsonProperty("sender_card_bank")
    String senderCardBank;
    @Enumerated(EnumType.STRING)
    @JsonProperty("sender_card_type")
    LiqpayCardType senderCardType;
    @JsonProperty("sender_card_country")
    Integer senderCardCountry;
    BigDecimal amount;
    String currency;
    @JsonProperty("receiver_commission")
    BigDecimal liqpayCommission;

    @JsonProperty("create_date")
    @JsonDeserialize(using = UnixToLocalDateTimeDeserializer.class)
    LocalDateTime createDate;
    @JsonProperty("end_date")
    @JsonDeserialize(using = UnixToLocalDateTimeDeserializer.class)
    LocalDateTime endDate;

}
