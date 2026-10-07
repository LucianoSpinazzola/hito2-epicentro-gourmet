package com.epicentrogourmet.hito2.entities;

import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "unidad_venta")
@Getter @Setter @NoArgsConstructor
public class UnidadVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUnidadVenta;

    private String codigo;        
    private String nombre;       
    private String descripcion;
    private String ubicacion;    
    private double superficie;    
    private String responsable;   

    @ManyToOne
    @JoinColumn(name = "id_festival")
    private Festival festival;

    // EL SET CON EL NOMBRE EXACTO DEL DIAGRAMA: platos
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "unidad_venta_plato",
        joinColumns = @JoinColumn(name = "id_unidad_venta"),
        inverseJoinColumns = @JoinColumn(name = "id_plato")
    )
    private Set<Plato> platos; 
}