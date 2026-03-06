package com.francocallero.sistema.de.gestion.academico.gestion_de_examenes;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class InstanciaExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInstanciaExamen;

    @ManyToMany
    @JoinTable(
            name = "instancia_examen_docente",
            joinColumns = @JoinColumn(name = "id_instancia_examen"),
            inverseJoinColumns = @JoinColumn(name = "id_docente")
    )
    private List<Docente> docentes;

    private LocalDateTime fechaHora;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instanciaExamen")
    private List<Examen> examenes;

    @ManyToOne
    @JoinColumn(name="id_materia")
    private Materia materia;

    private String sede;

    public InstanciaExamen(){
        this.docentes = new ArrayList<>();
        this.examenes = new ArrayList<>();
    }

    public boolean equals(InstanciaExamen instanciaExamen){
        return instanciaExamen.getFechaHora().equals(this.getFechaHora())
                && instanciaExamen.getMateria().equals(this.getMateria())
                && instanciaExamen.getSede().equalsIgnoreCase(this.getSede());
    }

    public void inscribirAAlumno(Alumno alumno){
        if(!this.puedeAnotarse(alumno) || !this.estaVigente()){
            throw  new BusinessException(ErrorCode.ALUMNO_EXAMEN_NO_PUEDE_INSCRIBIRSE,
                    this.getFechaHora());
        }else {
            Examen examen = new Examen();
            examen.setInstanciaExamen(this);
            examen.setAlumno(alumno);

            this.examenes.add(examen);
        }
    }

    public void agregarDocente(Docente docente){
        this.docentes.add(docente);
    }

    public boolean esAlumnoInscrito(Alumno alumno) {
        return this.examenes.stream()
                .anyMatch(examen -> examen.getAlumno().equals(alumno));
    }

    public boolean esDocente(Docente docente) {
        return this.docentes.contains(docente);
    }

    public boolean estaVigente() {
        return LocalDateTime.now().isBefore(this.fechaHora);
    }

    public boolean puedeAnotarse(Alumno alumno)  {


        return this.materia.cumpleConCorrelativasParaCursar(alumno) &&
                alumno.cursoMateria(this.materia) &&
                alumno.terminoDeCursar(this.materia) &&
                alumno.aproboMateria(this.materia) &&
                !alumno.promociono(this.materia) &&
                !this.esAlumnoInscrito(alumno);
    }

    public Integer getNotaDe(Alumno alumno) {
        if(!this.esAlumnoInscrito(alumno)){
            throw new BusinessException(ErrorCode.ALUMNO_EXAMEN_DE_OTRO);
        }

        Examen examen = this.getExamenDe(alumno);
        return examen.getNota();
    }

    public void setSede(String sede){
        this.sede = sede.toLowerCase();
    }

    public void eliminarInscripcionExamenDe(Alumno alumno) {
        Examen examen = this.getExamenDe(alumno);
        examen.setInstanciaExamen(null);

        this.examenes.remove(examen);
    }

    public void subirNotaDe(Alumno alumno, Integer nota, Docente docente) {

        if( !this.esDocente(docente) ||
                !this.esAlumnoInscrito(alumno) ||
                this.tieneNotaIgualA(alumno, nota)){
            throw new BusinessException(ErrorCode.DOCENTE_NO_PUEDE_SUBIR_NOTA_EXAMEN,
                    alumno.getNombreYApellido());
        }

        this.getExamenDe(alumno).setNota(nota);
    }

    public void limpiarExamenesSiTiene() {

        List<Examen> examenes = this.examenes;

        if(examenes.size() > 0 && examenes.stream().anyMatch(Examen::tieneNota)){
            examenes.forEach(examen -> {
                examen.setInstanciaExamen(null);
            });
        }
    }

    public boolean tieneNota(Alumno alumno) {
        Examen examen = this.getExamenDe(alumno);
        return examen.getNota() != null;
    }

    private Examen getExamenDe(Alumno alumno) {
        return this.examenes.stream()
                .filter(examen -> examen.getAlumno().equals(alumno))
                .findFirst()
                .orElseThrow(()-> new BusinessException(ErrorCode.INSTANCIA_EXAMEN_EXAMEN_NO_EXISTE,
                        alumno.getNombreYApellido()));
    }

    private boolean tieneNotaIgualA(Alumno alumno, Integer nota) {
         Examen examen = this.getExamenDe(alumno);
         Integer notaActual = examen.getNota();
        return notaActual != null && notaActual.equals(nota);
    }

}
