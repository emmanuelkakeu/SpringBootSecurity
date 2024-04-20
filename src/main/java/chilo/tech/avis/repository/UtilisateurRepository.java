package chilo.tech.avis.repository;

import chilo.tech.avis.entities.Utilisateur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UtilisateurRepository extends CrudRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByEmail(String email);
}
