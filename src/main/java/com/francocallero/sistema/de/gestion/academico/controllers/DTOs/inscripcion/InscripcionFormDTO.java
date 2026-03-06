package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscripcionFormDTO {

    @NotNull(message = "{alumnoId.null}")
    private Long alumnoId;

    @NotNull(message = "{cursoId.null}")
    private Long cursoId;
}

