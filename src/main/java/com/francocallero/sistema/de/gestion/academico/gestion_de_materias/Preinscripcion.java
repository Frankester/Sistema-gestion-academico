package com.francocallero.sistema.de.gestion.academico.gestion_de_materias;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Preinscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPreinscripcion;

    @ManyToOne
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name= "id_materia")
    private Materia materia;

    @Enumerated(EnumType.STRING)
    private EstadoPreInscripcion estadoPreInscripcion;

    private String comisionDeseada;

    public Preinscripcion() {
        this.estadoPreInscripcion= EstadoPreInscripcion.PENDIENTE;
    }

    public void aceptarPreInscripcion() {
        this.estadoPreInscripcion = EstadoPreInscripcion.ACEPTADA;
    }

    public void rechazarPreInscripcion() {
        this.estadoPreInscripcion = EstadoPreInscripcion.RECHAZADA;
    }


    public boolean estaPendiente(){
        return this.estadoPreInscripcion.equals(EstadoPreInscripcion.PENDIENTE);
    }

    public boolean esDeAlumno(Alumno alumno) {
        return this.alumno.equals(alumno);
    }
}
