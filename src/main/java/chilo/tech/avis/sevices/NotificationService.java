package chilo.tech.avis.sevices;

import chilo.tech.avis.entities.Validation;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {

    JavaMailSender javaMailSender;
    public void envoyer(Validation validation){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@emmanuelkakeu");
        message.setTo(validation.getUtilisateur().getEmail());
        message.setSubject("votre code d'activation est");

        String texte = String.format("Bonjour %s <br> Votre code d'activation est %s; A bientot",
                validation.getUtilisateur().getNom(),
        validation.getCode());
        message.setText(texte);
        javaMailSender.send(message);


    }
}
