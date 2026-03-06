package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamenListadoDocentesDTO extends ExamenListadoDTO{


    private String docentes; // ya formateado a 'Nombre Apellido, ...'
    private String sede;

}