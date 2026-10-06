package com.unla.hito2.entities;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idFestival;

    private String nombre;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}