package pl.clinic.project.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.clinic.project.model.Email;

@Service
@Slf4j
@AllArgsConstructor
public class MailService {

    private JavaMailSender mailSender;

    @Async
    public void sendMail(Email email) {
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("elicnic@gmail.com");
            messageHelper.setTo(email.getRecipient());
            messageHelper.setSubject(email.getSubject());
            messageHelper.setText(email.getBody());
        };
        try{
            mailSender.send(mimeMessagePreparator);
            log.info("Mail send to the user.");
        } catch (MailException e) {
            log.info(e.getMessage());
            throw new RuntimeException("Wystąpił błąd przy wysyłaniu maila.");
        }
    }

}
