package com.epicentrogourmet.hito2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entities.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {
}
