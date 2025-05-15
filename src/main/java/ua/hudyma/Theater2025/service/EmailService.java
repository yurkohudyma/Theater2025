package ua.hudyma.Theater2025.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import ua.hudyma.Theater2025.exception.EmailNotSentException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to ) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(SUBJECT);
            helper.setText(HTML_CONTENT, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Не вдалося надіслати email");
        }
    }

    static final String SUBJECT = "Ваш квиток до кінотеатру";
    //static final String HTML_CONTENT = "HTML";

    static final String HTML_CONTENT = """
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Ваш квиток підтверджено</title>
    <style>
        /* ...стилі тут... */
    </style>
</head>
<body>
<div class="email-container">
    <h2 class="header">Дякуємо за оплату!</h2>
    <p>Ваш квиток до кінотеатру успішно підтверджено. Ось деталі замовлення:</p>

    <table class="ticket-info">
        <tr><td><strong>Назва фільму:</strong></td><td>%s</td></tr>
        <tr><td><strong>Дата та час:</strong></td><td>%s</td></tr>
        <tr><td><strong>Місце:</strong></td><td>%s</td></tr>
        <tr><td><strong>Ціна:</strong></td><td>%s грн</td></tr>
        <tr><td><strong>Код замовлення:</strong></td><td>%s</td></tr>
    </table>

    <a href="%s" class="btn">Переглянути квиток</a>

    <div class="footer">
        &copy; 2025 Кінотеатр "Світло". До зустрічі на сеансі!
    </div>
</div>
</body>
</html>
""";
    //.formatted(movieTitle, dateTime, seatNumber, price, orderCode, ticketUrl);


    //".formatted(movieTitle, dateTime, seatNumber, price, orderCode, ticketUrl);

}
