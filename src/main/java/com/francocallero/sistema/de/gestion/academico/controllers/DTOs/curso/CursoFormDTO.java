package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CursoFormDTO {

    @NotNull(message = "{idMateria.null}")
    private Long idMateria;

    @NotNull(message = "{idDocente.null}")
    private Long idDocente;

    @NotBlank(message = "{comision.null}")
    private String comision;

    @NotNull(message = "{curso.fechaInicioCursada.emtpy}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicioCursada;

    @NotEmpty(message = "{curso.horariosCursada.emtpy}")
    @Valid
    private List<HorarioCursoDTO> horariosCursada;

    public CursoFormDTO(){
        this.horariosCursada = new ArrayList<>();
    }

    public void agregarHorario(HorarioCursoDTO horarioCursoDTO) {
        this.horariosCursada.add(horarioCursoDTO);
    }


    public void eliminarHorarioconIndice(int indiceHorario) {
        this.horariosCursada.remove(indiceHorario);
    }
}
