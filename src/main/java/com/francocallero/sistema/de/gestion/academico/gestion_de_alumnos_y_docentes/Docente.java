package com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes;

import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.persona.Rol;
import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="docente")
public class Docente extends Persona {

    @OneToMany(mappedBy = "docente")
    private List<Curso> cursos;


    public Docente(){
        this.cursos = new ArrayList<>();
        this.setRol(Rol.DOCENTE);
    }

    public void agregarCurso(Curso curso){
        this.cursos.add(curso);
    }

}
