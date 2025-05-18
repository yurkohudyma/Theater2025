package ua.hudyma.Theater2025.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ua.hudyma.Theater2025.exception.EmailNotSentException;
import ua.hudyma.Theater2025.model.SeatBatchRequest;
import ua.hudyma.Theater2025.model.SeatRequest;
import ua.hudyma.Theater2025.repository.HallRepository;
import ua.hudyma.Theater2025.repository.MovieRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final TicketService ticketService;
    @Value("${spring.mail.username}")
    private String fromEmail;
    static final String SUBJECT = "Ваш квиток до кінотеатру";

    public void sendEmail(String sendTo, SeatBatchRequest sbr, String txId) {
        var context = new Context();
        var movie = movieRepository.findById(sbr.movieId()).orElseThrow();
        var hall = hallRepository.findById(sbr.hallId()).orElseThrow();
        var seatsList = sbr.seats();
        var rowAndSeatNumber =
                seatsList.size() < 2
                        ? seatsList.get(0).seat() + "/" + seatsList.get(0).row()
                        : collectMultipleRowsAndSeats(seatsList);
        context.setVariables(Map.of(
                "movieTitle", movie.getName(),
                "hallName", hall.getName(),
                "date", LocalDate.now(), //todo insert real Date
                "time", sbr.timeslot(),
                "rowAndSeatNumber", rowAndSeatNumber,
                "price", hall.getSeatPrice(),
                "amount", hall.getSeatPrice() * seatsList.size()));
        processContextAndSend(sendTo, context, txId);
    }

    private String collectMultipleRowsAndSeats(List<SeatRequest> seatsList) {
        var sb = new StringBuilder();
        seatsList.forEach(s ->
                sb      .append(s.row())
                        .append("/")
                        .append(s.seat())
                        .append(" ")
                        );
        return sb.toString();
    }

    /*public void sendEmail(String sendTo, EmailMovieDTO dto) {
        var context = new Context();
        var date = dto.dateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        var time = dto.dateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        context.setVariables(Map.of(
                "movieTitle", dto.movieTitle(),
                "date", date,
                "time", time,
                "rowNumber", dto.rowNumber(),
                "seatNumber", dto.seatNumber(),
                "price", dto.price()));
        processContextAndSend(sendTo, dto, context);
    }*/

    private void processContextAndSend(String sendTo, Context context, String txId) {
        String htmlContent = templateEngine
                .process("email_ticket_template", context);
        MimeMessage message = mailSender.createMimeMessage();
        var qrBase64 = ticketService
                .generateQrBase64(txId);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8");
            helper.setTo(sendTo);
            helper.setSubject(SUBJECT);
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);
            helper.addInline("qrImage",
                    new ByteArrayResource(
                            qrBase64),
                    "image/png");
            mailSender.send(message);
            log.info("-------- mail has been successfully sent to {}", sendTo);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Не вдалося надіслати email");
        }
    }
}
