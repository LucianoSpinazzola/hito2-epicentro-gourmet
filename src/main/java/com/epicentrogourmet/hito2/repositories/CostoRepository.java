package com.epicentrogourmet.hito2.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entity.Costo;

@Repository
public interface CostoRepository extends JpaRepository<Costo, Long> {
	  Optional<Costo> findByFestivalIdFestival(Long idFestival);
}

