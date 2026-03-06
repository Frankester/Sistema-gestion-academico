package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen;

import lombok.Data;
import lombok.EqualsAndHashCode;



@Data
@EqualsAndHashCode(callSuper = true)
public class ExamenListadoAlumnosDTO extends ExamenListadoDTO {

    private Integer cantidadAlumnos;

}
