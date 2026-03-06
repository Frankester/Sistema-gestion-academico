package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class InstanciaExamenDTO extends ExamenListadoDTO{

    private String sede;
    private Boolean sePuedeEliminar;
    private Integer nota;

}
