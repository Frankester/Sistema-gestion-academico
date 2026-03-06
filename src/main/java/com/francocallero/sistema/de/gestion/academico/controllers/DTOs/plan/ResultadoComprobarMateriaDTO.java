package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan;

import lombok.Data;

@Data
public class ResultadoComprobarMateriaDTO {
    private Long resultadoMateriaId;
    private TipoComprobacion tipo;
    private Boolean puede;
    private String materiasQueNecesita;
}
