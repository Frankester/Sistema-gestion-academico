package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlanFormDTO {

    @NotBlank(message = "{plan.nombre.empty}")
    private String nombre;

    @NotNull(message = "{plan.carrera.empty}")
    private Long idCarrera;
}
