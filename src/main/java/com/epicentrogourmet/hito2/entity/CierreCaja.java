package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "cierre_caja")
@Getter
@Setter
@NoArgsConstructor
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCierre;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private double totalVentas;

    @Column(nullable = false)
    private int cantidadPedidos;

    @ManyToOne
    @JoinColumn(name = "id_unidad_venta", nullable = false)
    private UnidadDeVenta unidadDeVenta;

    @ManyToOne
    @JoinColumn(name = "id_cajero", nullable = false)
    private Empleado cajero;
}