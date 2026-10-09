package com.epicentrogourmet.hito2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entities.Costo;

@Repository
public interface CostoRepository extends JpaRepository<Costo, Long> {
}

