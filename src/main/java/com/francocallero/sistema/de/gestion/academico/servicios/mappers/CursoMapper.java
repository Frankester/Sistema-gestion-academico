package com.francocallero.sistema.de.gestion.academico.servicios.mappers;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoNotaCursoListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.*;
import com.francocallero.sistema.de.gestion.academico.controllers.utils.MessageUtils;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.HorarioCurso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Nota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CursoMapper {

    @Autowired
    private HorariosMapper mapperHorarios;

    public CursoListDTO transformToCursoList(Curso curso, Integer cantidadDeAlumnos) {
        CursoListDTO dto = new CursoListDTO();

        dto.setIdCurso(curso.getIdCurso());
        dto.setCantAlumnos(cantidadDeAlumnos);
        dto.setNombreMateria(curso.getMateria().getNombre());
        dto.setComision(curso.getComision());
        dto.setFechaInicio(curso.getFechaInicioCurso());

        Docente docente = curso.getDocente();
        dto.setNombreYApellidoDocente(docente.getNombreYApellido());

        dto.setHorarios(
                mapperHorarios.tranformarAString(curso.getHorariosDeCurso())
        );

        return dto;
    }
    public HorarioCurso transformHorarioCursoDTOToHorarioCurso(HorarioCursoDTO dto){
        HorarioCurso nuevoHorario = new HorarioCurso();
        nuevoHorario.setDiaDeLaSemana(dto.getDiaDeLaSemana());
        nuevoHorario.setHoraDesde(dto.getHoraDesde());
        nuevoHorario.setHoraHasta(dto.getHoraHasta());
        return nuevoHorario;
    }

    public CursoFormDTO transformarACursoForm(Curso curso){
        CursoFormDTO formCurso = new CursoFormDTO();
        formCurso.setComision(curso.getComision());
        formCurso.setIdMateria(curso.getMateria().getIdMateria());
        formCurso.setIdDocente(curso.getDocente().getIdPersona());
        formCurso.setFechaInicioCursada(curso.getFechaInicioCurso());


        curso.getHorariosDeCurso()
                .forEach((horarioCurso)->{
                    HorarioCursoDTO formHorario = this.transformarAHorarioCursoForm(horarioCurso);

                    formCurso.agregarHorario(formHorario);
                });

        return formCurso;
    }

    private HorarioCursoDTO transformarAHorarioCursoForm(HorarioCurso horario){

        HorarioCursoDTO formHorario = new HorarioCursoDTO();

        formHorario.setHoraDesde(horario.getHoraDesde());
        formHorario.setHoraHasta(horario.getHoraHasta());
        formHorario.setDiaDeLaSemana(horario.getDiaDeLaSemana());
        formHorario.setIdHorario(horario.getIdHorarioCurso());

        return formHorario;
    }

    public AlumnoNotaCursoListDTO transformarAAlumnoNotaCursoListDTO(Alumno alumno, Curso curso){
        AlumnoNotaCursoListDTO dto = new AlumnoNotaCursoListDTO();

        dto.setIdPersona(alumno.getIdPersona());
        dto.setNombreYApellido(alumno.getNombreYApellido());
        dto.setNotaFinal(
                alumno.getNotaDeMateria(curso.getMateria())
        );

        return dto;
    }

    public CursoAlumnoDTO transformarACursoAlumnoDTO(Curso curso, Alumno alumno) {
        CursoAlumnoDTO dto = new CursoAlumnoDTO();
        this.mapCursoAlumnoDTO(dto,curso, alumno);

        return dto;
    }

    public CursoAlumnoListDTO transformarACursoAlumnoListDTO(Curso curso, Alumno alumno) {
        CursoAlumnoListDTO dto = new CursoAlumnoListDTO();
        this.mapCursoAlumnoDTO(dto,curso, alumno);

        dto.setNotas(
                curso.getNotasDelAlumno(alumno).stream()
                        .map(this::transformToNotaList)
                        .toList()
        );
        dto.setHorariosCursada(
                mapperHorarios.tranformarAString(curso.getHorariosDeCurso())
        );

        dto.setNombreDocente(
                curso.getDocente().getNombreYApellido()
        );

        return dto;
    }

    public NotaListDTO transformToNotaList(Nota nota) {
        NotaListDTO dto = new NotaListDTO();
        dto.setNota(nota.getNota());
        dto.setFechaSubida(nota.getFechaSubida());
        dto.setIdNota(nota.getIdNota());
        return dto;
    }

    private void mapCursoAlumnoDTO(CursoAlumnoDTO dto, Curso curso, Alumno alumno){
        dto.setIdCurso(curso.getIdCurso());
        dto.setComision(curso.getComision());
        dto.setNombreMateria(curso.getMateria().getNombre());
        dto.setFechaInicioCursada(curso.getFechaInicioCurso());

        dto.setEstadoCursada(
                curso.getEstadoCursadaDe(alumno)
        );

        dto.setNotaFinal(
                curso.getNotaFinalDeAlumno(alumno)
        );
    }

}
