package com.epicentrogourmet.hito2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRecaudacionUnidadDTO {
    private String nombreComercial;
    private String codigo;
    private double totalRecaudado;
}