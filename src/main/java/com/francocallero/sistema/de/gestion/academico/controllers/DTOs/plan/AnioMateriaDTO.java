package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AnioMateriaDTO {

    private Integer anio;
    private List<MateriaDTO> materias;

    public AnioMateriaDTO(){
        this.materias=new ArrayList<>();
    }
}
