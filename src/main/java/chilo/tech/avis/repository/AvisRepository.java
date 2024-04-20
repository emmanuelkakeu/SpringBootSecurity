package chilo.tech.avis.repository;

import chilo.tech.avis.entities.Avis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisRepository extends CrudRepository<Avis, Integer> {
}
