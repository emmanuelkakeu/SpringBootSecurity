package chilo.tech.avis.sevices;


import chilo.tech.avis.entities.Avis;
import chilo.tech.avis.repository.AvisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AvisService {

    private final AvisRepository avisRepository;

    public void creer(Avis avis){
        this.avisRepository.save(avis);
    }


}
