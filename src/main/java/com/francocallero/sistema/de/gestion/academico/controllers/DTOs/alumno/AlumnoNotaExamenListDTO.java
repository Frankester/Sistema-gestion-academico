package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlumnoNotaExamenListDTO extends  AlumnoListDTO {

    private Integer notaExamen;
}
