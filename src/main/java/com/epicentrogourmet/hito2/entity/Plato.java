package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plato")
@Getter 
@Setter 
@NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlato;

    private String nombre;
    private double precioVenta;
    private double costoProduccion;

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plato plato = (Plato) o;
        return idPlato != null && idPlato.equals(plato.idPlato);
    }

    @Override
    public int hashCode() {
        return idPlato != null ? idPlato.hashCode() : 0;
    }
}