package com.francocallero.sistema.de.gestion.academico.gestion_de_cursos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class HorarioCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorarioCurso;

    @Enumerated(value=EnumType.STRING)
    private DiaDeLaSemana diaDeLaSemana;

    private Integer horaDesde;
    private Integer horaHasta;
}
