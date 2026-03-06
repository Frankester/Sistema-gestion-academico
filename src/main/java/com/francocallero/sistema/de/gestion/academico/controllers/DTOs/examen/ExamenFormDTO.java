package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExamenFormDTO {

    @NotNull(message = "{materiaId.null}")
    private Long materiaId;

    @NotNull(message = "{fecha.null}")
    @Future(message = "{fecha.past}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime fecha;

    @NotEmpty(message = "{examen.idDocentes.empty}")
    private List<Long> idDocentes;

    @NotBlank(message = "{examen.sede.blank}")
    private String sede;

    public ExamenFormDTO(){
        this.idDocentes=new ArrayList<>();
    }
}
