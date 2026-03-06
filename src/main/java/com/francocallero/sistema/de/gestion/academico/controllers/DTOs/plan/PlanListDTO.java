package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlanListDTO {
    private Long idPlan;
    private String nombre;
    private String carrera;

    private List<AnioMateriaDTO> anios;

    public PlanListDTO(){
        this.anios = new ArrayList<>();
    }
}
