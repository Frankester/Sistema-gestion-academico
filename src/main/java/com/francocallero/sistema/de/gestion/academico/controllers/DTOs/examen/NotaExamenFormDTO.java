package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class NotaExamenFormDTO {

    private Long idAlumno;
    private Long idInstanciaExamen;

    @NotNull(message = "{nota.null}")
    @Min(value = 1, message = "{nota.min}")
    @Max(value = 10, message = "{nota.max}")
    private Integer nota;


}
