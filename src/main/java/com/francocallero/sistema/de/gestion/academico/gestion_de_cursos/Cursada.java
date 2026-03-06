package com.francocallero.sistema.de.gestion.academico.gestion_de_cursos;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class Cursada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCursada;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="id_cursada")
    private List<Nota> notas;

    private Integer notaFinal;

    @ManyToOne
    @JoinColumn(name="id_curso")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name="id_alumno")
    private Alumno alumno;

    @Enumerated(value = EnumType.STRING)
    private EstadoCursada estado;


    public Cursada(){
        this.notas = new ArrayList<>();
        this.estado = EstadoCursada.EN_CURSO;
    }

    public void calcularNotaFinal() {

        if(this.notas.isEmpty()){
            throw new BusinessException(ErrorCode.ALUMNO_NO_TIENE_NOTAS);
        } else if(!this.cerroCursada()){
            throw new BusinessException(ErrorCode.DOCENTE_NO_PUEDE_SUBIR_NOTA_EXAMEN,
                    this.alumno.getNombreYApellido());
        }

        double promedioExacto = this.notas.stream()
                .collect(Collectors.averagingDouble(Nota::getNota));

        //redondeo para arriba
        int notaFinal =(int) Math.ceil(promedioExacto);

        this.setNotaFinal(notaFinal);
    }

    public void agregarNota(Nota nota){
        this.notas.add(nota);
    }

    public void quitarNota(Nota nota) {
        this.notas.remove(nota);
    }

    public boolean esDeLaMateria(Materia materia) {
        return curso.esDeLaMateria(materia);
    }

    public void cerrarCursada(){
        if(!this.estado.equals(EstadoCursada.EN_CURSO)){
            throw new BusinessException(ErrorCode.DOCENTE_NO_PUEDE_CERRAR_CURSADA);
        }else{
            this.setEstado(EstadoCursada.CERRADA);
        }
    }

    public void abandonarCursada(){
        if(!this.estado.equals(EstadoCursada.EN_CURSO)){
            throw new BusinessException(ErrorCode.DOCENTE_NO_PUEDE_CERRAR_CURSADA);
        }else{
            this.setEstado(EstadoCursada.CERRADA);
        }

        this.setEstado(EstadoCursada.ABANDONO);
    }

    public boolean seDioDeBaja() {
        return this.estado.equals(EstadoCursada.ABANDONO);
    }

    public boolean cerroCursada() {
        return this.estado.equals(EstadoCursada.CERRADA);
    }

}
