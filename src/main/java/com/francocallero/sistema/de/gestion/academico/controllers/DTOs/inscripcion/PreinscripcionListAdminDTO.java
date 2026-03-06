package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PreinscripcionListAdminDTO extends PreinscripcionDTO {
    private String nombreApellidoAlumno;

}
