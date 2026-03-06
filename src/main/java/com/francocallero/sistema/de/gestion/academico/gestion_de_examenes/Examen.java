package com.francocallero.sistema.de.gestion.academico.gestion_de_examenes;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamen;

    @ManyToOne
    @JoinColumn(name="id_instancia_examen")
    private InstanciaExamen instanciaExamen;
    private Integer nota;

    @ManyToOne
    @JoinColumn(name="id_alumno")
    private Alumno alumno;

    public Materia getMateria() {
        return this.instanciaExamen.getMateria();
    }

    public boolean aproboExamen() {
        return nota >= 6;
    }

    public boolean tieneNota() {
        return nota!=null;
    }

}
