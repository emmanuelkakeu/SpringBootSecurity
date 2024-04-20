package chilo.tech.avis.sevices;

import chilo.tech.avis.entities.Utilisateur;
import chilo.tech.avis.entities.Validation;
import chilo.tech.avis.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;


@Service
public class ValidationService {

    private ValidationRepository validationRepository;
    private NotificationService notificationService;

    public ValidationService(ValidationRepository validationRepository, NotificationService notificationService) {
        this.validationRepository = validationRepository;
        this.notificationService = notificationService;
    }

    public void enregistrer(Utilisateur utilisateur){

        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        Instant expiration = creation.plus(10, MINUTES);
        validation.setExpiration(expiration);
        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d",randomInteger);
        validation.setCode(code);
        this.validationRepository.save(validation);
        this.notificationService.envoyer(validation);


    }


    public Validation lireEnFonctionDuCode(String code){
        return this.validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("votre code est invalide"));
    }
}
