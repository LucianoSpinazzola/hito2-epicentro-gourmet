package com.epicentrogourmet.hito2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entity.Festival; 

@Repository
public interface FestivalRepository extends JpaRepository<Festival, Long> {
}