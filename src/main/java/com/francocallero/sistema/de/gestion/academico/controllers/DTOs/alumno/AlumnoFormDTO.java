package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlumnoFormDTO {

    @NotBlank(message = "{alumno.document.empty}")
    private String documento;
}
