package com.epicentrogourmet.hito2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {
    private Long idPlato;
    private String nombrePlato;
    private int cantidadTotalPedida;
    private double precioVenta;
}
