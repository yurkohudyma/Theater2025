package ua.hudyma.Theater2025.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ua.hudyma.Theater2025.dto.EmailMovieDTO;
import ua.hudyma.Theater2025.exception.EmailNotSentException;

import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;
    static final String SUBJECT = "Ваш квиток до кінотеатру";

    public void sendEmail(String sendTo, @Autowired EmailMovieDTO dto) {
        var context = new Context();
        context.setVariables(Map.of(
                "movieTitle", dto.movieTitle(),
                "dateTime", dto.dateTime(),
                "rowNumber", dto.rowNumber(),
                "seatNumber", dto.seatNumber(),
                "price", dto.price()));
        String htmlContent = templateEngine.process("email_ticket_template", context);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8");
            helper.setTo(sendTo);
            helper.setSubject(SUBJECT);
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);
            mailSender.send(message);
            log.info("-------- mail has been successfully sent to {}", sendTo);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Не вдалося надіслати email");
        }
    }
}
