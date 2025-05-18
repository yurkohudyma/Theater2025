package ua.hudyma.Theater2025.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.Theater2025.constants.TicketStatus;
import ua.hudyma.Theater2025.exception.QRCodeGenerationFailureException;
import ua.hudyma.Theater2025.model.Ticket;
import ua.hudyma.Theater2025.model.User;
import ua.hudyma.Theater2025.repository.TicketRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
@Log4j2
public class TicketService {

    private final TicketRepository ticketRepository;

    public static List<Map<String, Integer>> getTicketMap(List<Ticket> ticketList) {
        return ticketList.stream()
                .map(t -> Map.of(
                        "row", t.getRoww(),
                        "seat", t.getSeat()))
                .toList();
    }

    public LocalDateTime convertTimeSlotToLocalDateTime(String timeSlot) {
        LocalTime time = LocalTime.parse(timeSlot);
        return LocalDate.now().atTime(time);
    }

    public Optional<Ticket> getLastIssuedTicket(User user) {
        var ticketList = ticketRepository
                .findByUserIdAndTicketStatus(
                        user.getId(),
                        TicketStatus.PAID);
        return ticketList
                .stream()
                .max(comparing(Ticket::getId));
    }

    //@Scheduled(cron = "0 30 6 * * ?") //щодня о 6:30 ранку
    //@PostConstruct //а також при кожному запуску сервера

    @Transactional
    //@Modifying
    public void detectPendingTicketsAndDump() {
        var expiredList = ticketRepository.findAllPendingTickets();
        if (!expiredList.isEmpty()) {
            for (Ticket ticket : expiredList) {
                if (ticket.getScheduledOn().isBefore(LocalDateTime.now())) {
                    var ticketId = ticket.getId();
                    //ticketRepository.delete(ticket); //не працює
                    /** працює через EntityManager.remove(...)
                     * потребує, щоб об'єкт був у контексті persistence (managed)
                     * якщо ticket вже не прив'язаний (detached),
                     * або має зв’язки з об’єктами, які створюють проблеми —
                     * видалення тихо ігнорується або не тригериться
                     */
                    ticketRepository.deleteById(ticketId);
                    log.info("----expired ticket {} has been deleted", ticketId);
                } else {
                    log.info("----found PENDING and not EXPIRED ticket {}, skipping so far",
                            ticket.getScheduledOn());
                }
            }
        }
    }
    public byte[] generateQrBase64(String qrText) {
        try {
            int width = 200;
            int height = 200;
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 0); // Видаляє білу рамку
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(
                            qrText,
                            BarcodeFormat.QR_CODE,
                            width,
                            height,
                            hints);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);

            //generate img for control
            File outputfile = new File("res//ticket-qr.png");
            ImageIO.write(bufferedImage, "png", outputfile);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new QRCodeGenerationFailureException("Не вдалося згенерувати QR");
        }
    }

    public String generateQrImage64(String orderId) {
        var byteArray = generateQrBase64(orderId);
        return Base64.getEncoder().encodeToString(byteArray);
    }
}