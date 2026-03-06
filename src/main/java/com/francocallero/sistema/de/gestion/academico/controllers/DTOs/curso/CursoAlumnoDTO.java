package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CursoAlumnoDTO {

    private Long idCurso;
    private String nombreMateria;
    private String comision;
    private LocalDate fechaInicioCursada;
    private String estadoCursada;
    private Integer notaFinal;

}
