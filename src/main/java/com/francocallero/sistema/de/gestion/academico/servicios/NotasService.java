package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.NotaListDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Nota;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoNotas;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.CursoMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class NotasService {

    private final RepoNotas repoNotas;

    private final AlumnoService alumnoService;
    private final CursoService cursoService;
    private final CursoMapper cursoMapper;

    public NotaListDTO obtenerNotaParaEditar(Long idNota)  {
        Nota nota = this.obtenerNota(idNota);

        NotaListDTO notaListDTO = new NotaListDTO();
        notaListDTO.setNota(nota.getNota());
        return notaListDTO;
    }

    @Transactional
    public void cargarNotaA(Long idAlumno, Long idCurso, NotaListDTO form)  {
        Curso curso = cursoService.obtenerCursoConId(idCurso);
        Alumno alumno=alumnoService.obtenerAlumnoConId(idAlumno);

        Nota nuevaNota = new Nota();
        nuevaNota.setNota(form.getNota());
        nuevaNota.setFechaSubida(LocalDate.now());

        curso.agregarNotaA(alumno, nuevaNota);

        cursoService.guardarCurso(curso);
    }

    @Transactional
    public void removeNotaDeAlumnoDelCurso(Long idNota, Long idAlumno, Long idCurso)  {

        Curso curso = cursoService.obtenerCursoConId(idCurso);
        Alumno alumno = alumnoService.obtenerAlumnoConId(idAlumno);
        Nota nota = this.obtenerNota(idNota);
        curso.eliminarNotaDe(alumno,nota);

        cursoService.guardarCurso(curso);//update curso
        repoNotas.deleteById(idNota);
    }

    @Transactional
    public List<NotaListDTO> findAllNotasParaMostrarDeAlumno(Long idAlumno, Long idCurso) {
        Curso curso = cursoService.obtenerCursoConId(idCurso);
        Alumno alumno = alumnoService.obtenerAlumnoConId(idAlumno);

        List<Nota> notas = curso.getNotasDelAlumno(alumno);

        return notas.stream()
                .map(cursoMapper::transformToNotaList)
                .toList();
    }

    @Transactional
    public void actualizarNotaA(Long idAlumno, Long idCurso, NotaListDTO form, Long idNota) {
        Curso curso = cursoService.obtenerCursoConId(idCurso);
        Alumno alumno=alumnoService.obtenerAlumnoConId(idAlumno);

        if(!curso.esAlumno(alumno)){
            throw new BusinessException(ErrorCode.ALUMNO_NO_PERTENECE_CURSO);
        }
        if(!curso.isVirgente()){
            throw new BusinessException(ErrorCode.DOCENTE_CURSO_NO_PUEDE_CAMBIAR_NOTA);
        }
        if(curso.cerroCursadaDe(alumno)){
            throw new BusinessException(ErrorCode.DOCENTE_CURSADA_NO_PUEDE_CAMBIAR_NOTA);
        }

        Nota nota = this.obtenerNota(idNota);;
        nota.setNota(form.getNota());
        nota.setFechaSubida(LocalDate.now());

        repoNotas.save(nota);
    }


    //Utils

    private Nota obtenerNota(Long idNota) {
        return this.repoNotas.findById(idNota)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTA_NO_EXISTE));
    }

}
