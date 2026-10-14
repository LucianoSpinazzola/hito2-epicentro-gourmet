package com.epicentrogourmet.hito2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaDTO {
    private Long idFestival;
    private String nombreFestival;
    private double recaudacionTotal;
}
