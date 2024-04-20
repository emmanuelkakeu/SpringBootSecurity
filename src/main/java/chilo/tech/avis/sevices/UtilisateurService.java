package chilo.tech.avis.sevices;

import chilo.tech.avis.TypeRole;
import chilo.tech.avis.entities.Role;
import chilo.tech.avis.entities.Utilisateur;
import chilo.tech.avis.entities.Validation;
import chilo.tech.avis.repository.UtilisateurRepository;


import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ValidationService validationService;




    public void inscription(Utilisateur utilisateur){


        if (!utilisateur.getEmail().contains("@")){
            throw new RuntimeException("votre mail est invalide");
        }
        if (!utilisateur.getEmail().contains(".")){
            throw new RuntimeException("votre mail est invalide");
        }
        Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (utilisateurOptional.isPresent()){
            throw new RuntimeException("votre mail est deja utiliser");
        }
        String mdpCrypte = this.passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpCrypte);
        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);
         utilisateur = this.utilisateurRepository.save(utilisateur);
         this.validationService.enregistrer(utilisateur);

    }

    public void activation(Map<String, String> activation) {
        Validation validation =this.validationService.lireEnFonctionDuCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("votre code à expired");
        }
       Utilisateur utilisateurActiver = this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(() -> new RuntimeException("l'utilisateur est inconnu"));
       utilisateurActiver.setActif(true);
       this.utilisateurRepository.save(utilisateurActiver);
    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("aucun utilisateur ne correspond a cet identifient."));
    }
}
