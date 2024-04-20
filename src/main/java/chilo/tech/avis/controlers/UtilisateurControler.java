package chilo.tech.avis.controlers;

import chilo.tech.avis.dto.AuthentificationDTO;
import chilo.tech.avis.entities.Utilisateur;
import chilo.tech.avis.securite.JwtService;
import chilo.tech.avis.sevices.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurControler {
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private UtilisateurService utilisateurService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur){
        log.info("inscrit");

        this.utilisateurService.inscription(utilisateur);
        ;
    }
    @PostMapping(path = "activation")
    public void activation(@RequestBody  Map<String, String> activation){

        this.utilisateurService.activation(activation);
        log.info("compte actif");
    }
    @PostMapping(path = "/deconnexion")
    public void deconnexion(){
        this.jwtService.deconnexion();
    }

    @PostMapping(path = "/refreschToken")
    public @ResponseBody Map<String, String> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        return this.jwtService.refreshToken(refreshTokenRequest);
    }

    @PostMapping(path = "connexion")
    public Map<String , String > connexion(@RequestBody AuthentificationDTO authentificationDTO){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );

        if (authenticate.isAuthenticated()){
            log.info("connecter");
            return this.jwtService.generate(authentificationDTO.username());

        }

        return null;
    }
}
