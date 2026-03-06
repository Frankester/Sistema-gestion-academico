package com.francocallero.sistema.de.gestion.academico.gestion_de_cursos;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCurso;

    @ManyToOne
    @JoinColumn(name="id_materia")
    private Materia materia;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Cursada> cursadas;

    @ManyToOne
    @JoinColumn(name="id_docente")
    private Docente docente;

    @Column(unique = true, nullable = false)
    private String comision;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name= "id_curso")
    private List<HorarioCurso> horariosDeCurso;

    private boolean virgente;
    private LocalDate fechaInicioCurso;


    public Curso() {
        this.cursadas = new ArrayList<>();
        this.virgente=true;
        this.horariosDeCurso = new ArrayList<>();
    }


    public boolean equals(Curso curso){
        return curso.getMateria().equals(this.getMateria()) &&
                curso.getComision().equalsIgnoreCase(this.comision);
    }

    public void agregarHorarioCursada(HorarioCurso nuevoHorario) {
        this.horariosDeCurso.add(nuevoHorario);
    }

    public List<Nota> getNotasDelAlumno(Alumno alumno){
        Cursada cursada = getCursadaDe(alumno);

        return cursada.getNotas();
    }

    private Cursada getCursadaDe(Alumno alumno){
        return this.cursadas.stream()
                .filter(cursadaExistente->cursadaExistente.getAlumno().equals(alumno))
                .findFirst()
                .orElseThrow(()->new BusinessException(ErrorCode.ALUMNO_NO_PERTENECE_CURSO, comision));
    }

    public void eliminarNotaDe(Alumno alumno, Nota nota) {
        Cursada cursada = this.getCursadaDe(alumno);
        cursada.quitarNota(nota);
    }

    public void agregarNotaA(Alumno alumno, Nota nuevaNota) {
        Cursada cursada = this.getCursadaDe(alumno);

        cursada.agregarNota(nuevaNota);
    }

    public void inscribirAlumno(Alumno alumno) {

        if(this.esAlumno(alumno)){
            throw new BusinessException(ErrorCode.ALUMNO_YA_ESTA_MATRICULADO);
        }else  if(!this.materia.cumpleConCorrelativasParaCursar(alumno)) {
            throw new BusinessException(ErrorCode.ALUMNO_NO_PUEDE_INSCRIBIRSE, this.materia.getNombre());
        }else{
            Cursada cursada = new Cursada();
            cursada.setAlumno(alumno);
            cursada.setCurso(this);

            this.cursadas.add(cursada);
        }
    }

    public boolean esAlumno(Alumno alumno) {
        return this.cursadas.stream()
                .anyMatch(cursada -> cursada.getAlumno().equals(alumno) &&
                        !cursada.seDioDeBaja());
    }

    public Integer getNotaFinalDeAlumno(Alumno alumno) {
        Cursada cursada = this.getCursadaDe(alumno);

        return cursada.getNotaFinal();
    }


    public boolean esDeLaMateria(Materia materia) {
        return this.materia.equals(materia);
    }

    public void darDeBajaA(Alumno alumno) {
        if(!this.esAlumno(alumno)){
            throw new BusinessException(ErrorCode.ALUMNO_NO_PERTENECE_CURSO, this.comision);
        } else {
            Cursada cursada = this.getCursadaDe(alumno);
            cursada.abandonarCursada();
        }
    }

    public List<Alumno> getAlumnosInscriptos() {
        return this.cursadas.stream()
                .filter(cursada -> !cursada.seDioDeBaja())
                .map(Cursada::getAlumno)
                .toList();
    }

    public Boolean cerroCursadaDe(Alumno alumno) {
        Cursada cursada = this.getCursadaDe(alumno);
        return cursada.cerroCursada();
    }

    public void cerrarCursadaDe(Alumno alumno) {
        Cursada cursada = this.getCursadaDe(alumno);
        cursada.cerrarCursada();
        cursada.calcularNotaFinal();
    }

    public String getEstadoCursadaDe(Alumno alumno) {
        Cursada cursada = this.getCursadaDe(alumno);

        return cursada.getEstado().toString();
    }

    public boolean esDocente(Docente docente) {
        return this.getDocente().equals(docente);
    }
}
