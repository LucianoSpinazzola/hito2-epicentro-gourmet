package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "costo")
@Getter @Setter @NoArgsConstructor
public class Costo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCosto;

    private double sueldoBase;           
    private double costoSuperficie;      
    private double plusElectricidad;     
    private double costoMontaje;         
    private double valorAntiguedad;      

    @OneToOne
    @JoinColumn(name = "id_festival")
    private Festival festival; 
}