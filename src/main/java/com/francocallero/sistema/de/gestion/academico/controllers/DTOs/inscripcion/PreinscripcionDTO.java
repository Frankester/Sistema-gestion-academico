package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion;

import lombok.Data;

@Data
public class PreinscripcionDTO {
    private Long idPreinscripcion;

    private String nombreMateria;
    private String comision;
    private String estado;
}
