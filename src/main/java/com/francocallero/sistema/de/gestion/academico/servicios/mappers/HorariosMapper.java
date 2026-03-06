package com.francocallero.sistema.de.gestion.academico.servicios.mappers;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.MessageUtils;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.HorarioCurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HorariosMapper {


    @Autowired
    private MessageUtils messageUtils;

    public String tranformarAString(List<HorarioCurso> horariosCursos){
        return horariosCursos.stream()
                .map(horario ->
                        this.messageUtils.getMessage("horario.format",
                                horario.getDiaDeLaSemana().toString(),
                                horario.getHoraDesde(),
                                horario.getHoraHasta()))
                .collect(Collectors.joining(",\n"));
    }
}
