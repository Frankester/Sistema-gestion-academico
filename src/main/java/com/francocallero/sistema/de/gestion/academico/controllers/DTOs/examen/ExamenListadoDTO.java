package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class ExamenListadoDTO {

    private Long idExamen;
    private String materiaNombre;
    private LocalDateTime fecha;
}
