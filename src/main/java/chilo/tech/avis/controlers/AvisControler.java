package chilo.tech.avis.controlers;

import chilo.tech.avis.entities.Avis;
import chilo.tech.avis.sevices.AvisService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@AllArgsConstructor
@RequestMapping("avis")
@Controller
public class AvisControler {

    private  final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping

    public  void creer(@RequestBody Avis avis){
        this.avisService.creer(avis);
    }
}
