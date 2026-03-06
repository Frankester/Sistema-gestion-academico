package com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes;

import com.francocallero.sistema.de.gestion.academico.exceptions.*;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Cursada;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import com.francocallero.sistema.de.gestion.academico.persona.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="alumno")
public class Alumno extends Persona {

    @OneToMany(mappedBy = "alumno")
    private List<Cursada> cursadas;

    @ManyToOne
    @JoinColumn(name="id_plan")
    private Plan planInscripto;



    public Alumno(){
        this.cursadas = new ArrayList<>();
        this.setRol(Rol.ALUMNO);
    }


    public List<Materia> materiasQuePuedeInscribirse(){
        List<Materia> materiasDelPlan =  this.planInscripto.getMaterias();

        return materiasDelPlan.stream()
                .filter(materia ->this.puedeCursarMateria(materia) && !this.cursoMateria(materia))
                .toList();
    }

    public Integer getNotaDeMateria(Materia materia) {
        Curso curso = this.ultimoCursoDeLaMateria(materia);
        return curso.getNotaFinalDeAlumno(this);
    }

    public int getNotaDeExamen(InstanciaExamen instanciaExamen)  {
        return instanciaExamen.getNotaDe(this);
    }

    public boolean aproboMateria(Materia materia) {
        return this.getNotaDeMateria(materia) >= 6;
    }

    public boolean terminoDeCursar(Materia materia) {
        Curso curso = this.ultimoCursoDeLaMateria(materia);
        return curso.cerroCursadaDe(this);
    }

    public boolean cursoMateria(Materia materia) {
        return !this.obtenerCursosDeMateria(materia).isEmpty();
    }


    public boolean promociono(Materia materia) {
        return this.getNotaDeMateria(materia) >= 8;
    }

    private boolean puedeCursarMateria(Materia materia){
        return materia.cumpleConCorrelativasParaCursar(this);
    }

    private Curso ultimoCursoDeLaMateria(Materia materia) {
        return this.obtenerCursosDeMateria(materia)
                .stream()
                .max(Comparator.comparing(Curso::getFechaInicioCurso))
                .orElseThrow(()->
                        new BusinessException(ErrorCode.ALUMNO_NO_HIZO_LA_MATERIA, materia.getNombre()));
    }

    private List<Curso> obtenerCursosDeMateria(Materia materia){
        return this.cursadas.stream()
                .filter(cursada -> cursada.esDeLaMateria(materia))
                .map(Cursada::getCurso)
                .toList();
    }



}
