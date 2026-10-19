package com.epicentrogourmet.hito2.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFormDTO {
    private Long idPlato;
    private String nombrePlato;
    private double precioVenta;
    private int cantidad;
}