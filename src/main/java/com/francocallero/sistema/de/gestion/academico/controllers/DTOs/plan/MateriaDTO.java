package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MateriaDTO {

    private Long idMateria;

    @NotBlank(message = "{materia.empty}")
    private String nombreMateria;

    @NotNull(message = "{materia.carrera.empty}")
    private Long idCarrera;

    @Min(value = 1, message = "{materia.nivel.empty}")
    private Integer anioPerteneciente;

    private List<Long> idsCorrelativas;

    private Boolean aprobo;
    private Boolean curso;

    public MateriaDTO(){
        this.idsCorrelativas = new ArrayList<>();
    }
}
