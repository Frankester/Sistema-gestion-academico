package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PreinscripcionAlumnoDTO extends PreinscripcionDTO {

    private Long idCurso;
    private String horariosCursada;
    private boolean conflictoHorario;

}
