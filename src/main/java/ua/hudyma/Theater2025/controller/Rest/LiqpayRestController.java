package ua.hudyma.Theater2025.controller.Rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.constants.liqpay.LiqPayAction;
import ua.hudyma.Theater2025.exception.RefundFailureException;
import ua.hudyma.Theater2025.model.Seat;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.Transaction;
import ua.hudyma.Theater2025.payment.LiqPayHelper;
import ua.hudyma.Theater2025.repository.*;
import ua.hudyma.Theater2025.service.*;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.liqpay.LiqPayApi.API_VERSION;

@RestController
@Log4j2
@RequiredArgsConstructor
public class LiqpayRestController {
    public static final String API_REQUEST = "https://www.liqpay.ua/api/request", SIGNATURE = "signature", DATA = "data";
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final PaymentService paymentService;
    private final HallRepository hallRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final OrderService orderService;
    private final EmailService emailService;
    @Value("${liqpay_public_key}")
    private String publicKey;
    @Value("${liqpay_private_key}")
    private String privateKey;

    @PostMapping("/liqpay-callback")
    @SneakyThrows
    public void handleCallback(@RequestParam Map<String, String> body) {
        String data = body.get(DATA);
        String signature = body.get(SIGNATURE);
        String decodedJson = paymentService.getDecodedJson(data);

        if (paymentService.verifySignature(data, signature)) {
            log.info("...........LiqPay payment SUCCESSFULL, callback received");
            log.info(decodedJson);
            issueTicketsAndReserveSeats(decodedJson);
        } else {
            log.error("........Wrong signature. Potential spoofing attempt.");
            log.info(decodedJson);
        }
    }

    private void issueTicketsAndReserveSeats(String decodedJson) throws JsonProcessingException {
        Transaction transaction = new ObjectMapper()
                .readValue(decodedJson, Transaction.class);
        var order = orderService.retrieveOrderFromInMemoryMap(
                transaction.getLocalOrderId()).orElseThrow();
        var seatBatchRequest = order.requestedSeats();
        var seatRequest = seatBatchRequest.seats();
        var hall = hallRepository.findById(seatBatchRequest.hallId()).orElseThrow();
        var movie = movieRepository.findById(seatBatchRequest.movieId()).orElseThrow();
        var user = userRepository.findById(seatBatchRequest.userId()).orElseThrow();
        var timeSlotToLocalDateTime =
                ticketService.convertTimeSlotToLocalDateTime(seatBatchRequest.timeslot());
        var movieDate = new AtomicReference<>(LocalDate.now());
        seatRequest.forEach(sr -> {
            var seat = Seat.builder()
                    .hall(hall)
                    .price(hall.getSeatPrice())
                    .isOccupied(true)
                    .seatNumber(sr.seat())
                    .rowNumber(sr.row())
                    .createdOn(LocalDateTime.now())
                    .build();
            seatRepository.save(seat);
            log.info("---------new seat {} fixed", seat.getId());

            var ticket = Ticket.builder()
                    .value(hall.getSeatPrice())
                    .ticketStatus(TicketStatus.PAID)
                    .roww(sr.row())
                    .seat(sr.seat())
                    .orderId(order.orderId())
                    .user(user)
                    .hall(hall)
                    .movie(movie)
                    .scheduledOn(timeSlotToLocalDateTime)
                    .purchasedOn(LocalDateTime.now())
                    .build();
            movieDate.set(ticket.getScheduledOn().toLocalDate());

            transactionService.bindTransactionWithTickets(transaction, ticket);
            ticketRepository.save(ticket);
            log.info("---------new ticket {} fixed", ticket.getId());
        });
        transactionService.addNewTransaction(transaction);
        emailService.sendEmail(
                user.getEmail(),
                seatBatchRequest,
                transaction.getLocalOrderId(),
                movieDate.get());
    }

    /**
     * ендпойнт для повернення платежів, в режимі sandbox не працює.
     * Відповідь прийде на зареєстрований в liqpay endpoint
     * і її треба також розшифровувати і парсити. Статус платежу має бути <b>REFUND</b>
     */
    @PostMapping("/refund/{id}")
    public String refundPayment(@PathVariable("id") Long id) {
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
            request.put(DATA, Collections.singletonList(paymentData));
            request.put(SIGNATURE, Collections.singletonList(signature));
            RestTemplate template = new RestTemplate();

            var refundTransaction = Transaction
                    .builder()
                    .createDate(LocalDateTime.now())
                    .description("Повернення квитка")
                    .localOrderId(transaction.getLocalOrderId())
                    .build();

            refundTransaction.getTickets().add(ticket);
            ticket.getTransactions().add(refundTransaction);
            ticket.setTicketStatus(TicketStatus.REFUNDED);
            ticketRepository.save(ticket);
            refundTransaction.setAction(LiqPayAction.REFUND);
            transactionRepository.save(refundTransaction);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> liqRequest =
                    new HttpEntity<>(request, headers);
            return template.postForObject(
                    API_REQUEST,
                    liqRequest,
                    String.class);
        } catch (NoSuchAlgorithmException e) {
            throw new RefundFailureException("------повернення не відбулось");
        }
    }

    /**
     *
     * @param ticketId
     * in SANDBOX mode returns 'SANDBOX' status and TX payment data
     * Для отримання реального статусу потрібно переводити систему в бойовий режим
     */
    @PostMapping("/payment_status/{ticketId}") //
    public ResponseEntity<String> paymentStatus(@PathVariable("ticketId") Long ticketId) throws Exception {
        var transaction = transactionService.getByTicketIdAndAction(ticketId);

        var params = new LinkedHashMap<>(
                Map.of("action", "status",
                        "version", API_VERSION,
                        "public_key", publicKey,
                        "order_id", transaction.getLocalOrderId()));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(params);
        String data = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String signature = LiqPayHelper.getPaymentSignature(data, privateKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(DATA, data);
        body.add(SIGNATURE, signature);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(API_REQUEST,
                request, String.class);

        Map<String, Object> result = objectMapper.readValue(
                response.getBody(),  new TypeReference<>(){});
        String status = (String) result.get("status");
        log.info("======LiqPay status for order {}: {}", transaction.getLocalOrderId(), status);

        return ResponseEntity.ok(result.toString());
    }
}