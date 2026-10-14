package com.epicentrogourmet.hito2.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epicentrogourmet.hito2.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
	
	Optional<Rol> findByNombreRol(String nombreRol);
}