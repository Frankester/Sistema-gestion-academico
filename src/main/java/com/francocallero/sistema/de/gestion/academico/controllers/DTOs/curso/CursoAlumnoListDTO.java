package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CursoAlumnoListDTO extends CursoAlumnoDTO{
    private List<NotaListDTO> notas;

    private String horariosCursada;
    private String nombreDocente;

}
