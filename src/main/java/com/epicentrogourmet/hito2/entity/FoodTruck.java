package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "food_truck")
@DiscriminatorValue("FOOD_TRUCK")
@Getter
@Setter
@NoArgsConstructor
public class FoodTruck extends UnidadDeVenta {

	@Column(nullable = false, unique = true)
	private String patente;

	@Column(nullable = false)
	private boolean requiereConexionElectrica;

}
