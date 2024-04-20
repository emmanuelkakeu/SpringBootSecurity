package chilo.tech.avis.repository;

import chilo.tech.avis.entities.Jwt;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {


    Optional<Jwt> findByValeurAndDesactiverAndExpired(String valeur, boolean desactiver, boolean expired);

    @Query("FROM Jwt j WHERE j.utilisateur.email = :email")
    Stream<Jwt> findUtilisateur(String email);

    @Query("FROM Jwt j WHERE j.refreshToken.valeur = :valeur")
    Optional<Jwt> findByRefreshToken(String valeur);

    @Query("FROM Jwt j WHERE j.expired = :expired AND j.desactiver = :desactiver AND j.utilisateur.email= :email")
   Optional<Jwt> findUtilisateurValidToken(String email, boolean desactiver, boolean expired);

    void deleteAllByExpiredAndDesactiver(boolean expired, boolean desactiver);
}
