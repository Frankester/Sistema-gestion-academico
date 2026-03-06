package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso;

import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.DiaDeLaSemana;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HorarioCursoDTO {

    private Long idHorario;

    @NotNull(message = "{curso.horariosCursada.horaDesde.emtpy}")
    @Min(value = 0, message = "{curso.horariosCursada.hora.invalid-format}")
    @Max(value=23, message = "{curso.horariosCursada.hora.invalid-format}")
    private Integer horaDesde;

    @NotNull(message = "{curso.horariosCursada.horaHasta.emtpy}")
    @Min(value = 0, message = "{curso.horariosCursada.hora.invalid-format}")
    @Max(value=23, message = "{curso.horariosCursada.hora.invalid-format}")
    private Integer horaHasta;

    @NotNull(message = "{curso.horariosCursada.diaDeLaSemana.emtpy}")
    private DiaDeLaSemana diaDeLaSemana;
}
