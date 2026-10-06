package com.epicentrogourmet.hito2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "festival")
@Getter
@Setter
@NoArgsConstructor
public class Festival {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idFestival;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String temporada;

	@Column(nullable = false)
	private LocalDate fechaDeInicio;

	@Column(nullable = false)
	private LocalDate fechaDeFin;

}
