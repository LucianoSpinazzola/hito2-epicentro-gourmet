package com.epicentrogourmet.hito2.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroUnidadDTO {
    // Datos de la Unidad
    private String nombreComercial;
    private Double superficie;
    private String codigo;
    private String tipoUnidad; // "FOOD_TRUCK" o "PUESTO_DESARMABLE"
    
    // Específicos FoodTruck
    private String patente;
    private boolean requiereConexionElectrica;
    
    // Específicos PuestoDesarmable
    private Integer cantidadCarpas;
    private Integer tiempoMontaje;

    // Datos del Festival
    private Long idFestival;

    // Datos del Responsable (Empleado + Usuario)
    private String nombreResponsable;
    private String apellidoResponsable;
    private Integer dniResponsable;
    private String emailResponsable;
    private String passwordResponsable;
}
