package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.carrera;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarreraDTO {

    private Long idCarrera;

    @NotBlank(message = "{carrera.empty}")
    private String nombreCarrera;

}
