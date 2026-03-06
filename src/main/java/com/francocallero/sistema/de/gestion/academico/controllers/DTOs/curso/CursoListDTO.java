package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CursoListDTO {

    private Long idCurso;

    private String nombreMateria;
    private Integer cantAlumnos;
    private String comision;
    private String horarios;
    private String nombreYApellidoDocente;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
}
