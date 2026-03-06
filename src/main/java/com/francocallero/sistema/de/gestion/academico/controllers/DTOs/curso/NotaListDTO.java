package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NotaListDTO {

    private Long idNota;

    private LocalDate fechaSubida;

    @NotNull(message = "{nota.null}")
    @Min(value = 1, message = "{nota.min}")
    @Max(value = 10, message = "{nota.max}")
    private Integer nota;
}
