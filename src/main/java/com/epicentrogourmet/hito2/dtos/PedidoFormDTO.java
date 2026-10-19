package com.epicentrogourmet.hito2.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoFormDTO {
    private Long idUnidadVenta;
    private List<ItemFormDTO> items = new ArrayList<>();
}
