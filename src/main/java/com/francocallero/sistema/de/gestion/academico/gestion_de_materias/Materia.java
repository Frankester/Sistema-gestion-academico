package com.francocallero.sistema.de.gestion.academico.gestion_de_materias;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Carrera;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMateria;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private Plan plan;

    @ManyToMany
    @JoinTable(
            name = "materia_correlativa",
            joinColumns = @JoinColumn(name = "id_materia"),
            inverseJoinColumns = @JoinColumn(name = "id_correlativa")
    )
    private List<Materia> correlativas;

    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    private Integer anioPerteneciente;

    public Materia() {
        this.correlativas = new ArrayList<>();
    }

    public boolean cumpleConCorrelativasParaCursar(Alumno alumno){

        return this.correlativas.stream()
                .allMatch(alumno::cursoMateria);
    }

    public boolean cumpleConCorrelativasParaAprobar(Alumno alumno) {

        return this.correlativas.stream()
                .allMatch(materia -> alumno.cursoMateria(materia) &&
                        alumno.terminoDeCursar(materia) &&
                        alumno.aproboMateria(materia));
    }

    public boolean equals(Materia materia){
        return materia.getNombre().equals(this.getNombre()) &&
                materia.getPlan().equals(this.getPlan());
    }


    public void agregarCorrelativa(Materia materiaCorrelativa) {
        if(this.existeCorrelativa(materiaCorrelativa)){
            throw new BusinessException(ErrorCode.MATERIA_CORRELATIVA_YA_EXISTE,
                    materiaCorrelativa.getNombre());
        }else{
            correlativas.add(materiaCorrelativa);
        }
    }

    public List<Materia> obtenerMateriasQueNecesitaParaCursar(Alumno alumno) {
        return this.correlativas.stream()
                .filter(materia->!alumno.cursoMateria(materia))
                .toList();
    }

    public List<Materia> obtenerMateriasQueNecesitaParaAprobar(Alumno alumno) {
        return this.correlativas.stream()
                .filter(materia-> !alumno.cursoMateria(materia) ||
                        !alumno.terminoDeCursar(materia) ||
                        !alumno.aproboMateria(materia))
                .toList();
    }

    private boolean existeCorrelativa(Materia materiaCorrelativa) {
        return this.correlativas.contains(materiaCorrelativa);
    }

}