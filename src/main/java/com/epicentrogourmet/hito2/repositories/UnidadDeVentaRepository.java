package com.epicentrogourmet.hito2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entity.UnidadDeVenta;

@Repository
public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {
	
	List<UnidadDeVenta> findByFestivalIdFestivalAndEliminadoFalse(Long idFestival);
	List<UnidadDeVenta> findByEliminadoFalse();
}

