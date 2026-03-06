package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion.PreinscripcionAlumnoDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.HorarioCurso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Preinscripcion;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoPreInscripciones;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.HorariosMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionCursoAlumnoService {

    private final AlumnoService alumnoService;
    private final CursoService cursoService;
    private final UsuarioService usuarioService;
    private final RepoPreInscripciones repoPreInscripciones;

    private final HorariosMapper horariosMapper;

    @Transactional
    public List<PreinscripcionAlumnoDTO> obtenerMateriasQuePuedePreinscribirseAlumnoParaMostrar(
            Usuario usuarioAlumno
    ) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuarioAlumno);

        List<Materia> materiasQuePuedeInscribirse = alumno.materiasQuePuedeInscribirse();

        List<Curso> cursosQuePuedeAnotarse = cursoService.obtenerCursosDisponiblesDeMaterias(
                materiasQuePuedeInscribirse
        );

        return cursosQuePuedeAnotarse.stream()
                .filter(curso -> !curso.esAlumno(alumno) &&
                        !repoPreInscripciones.existsPreinscripcionConComisionYMateria(
                                curso.getComision(),
                                curso.getMateria().getIdMateria(),
                                alumno.getIdPersona()) &&
                        !repoPreInscripciones.existsPreinscripcionPendienteConIdMateria(
                                curso.getMateria().getIdMateria())
                )
                .map((cursoQuePuedeAnotarse)-> {
                    PreinscripcionAlumnoDTO dto = this.transformarAPreinscripcionAlumnoDTO(cursoQuePuedeAnotarse);

                    dto.setConflictoHorario(this.hayConflictoHorarios(
                            cursoQuePuedeAnotarse.getHorariosDeCurso(),
                            alumno.getIdPersona())
                    );

                    return dto;
                })
                .toList();
    }


    @Transactional
    public void preinscribirAlumno(Usuario usuarioAlumno, Long idCurso) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuarioAlumno);
        Curso curso = cursoService.obtenerCursoConId(idCurso);
        Materia materia = curso.getMateria();

        if(this.hayConflictoHorarios(curso.getHorariosDeCurso(),alumno.getIdPersona())){
            throw new BusinessException(ErrorCode.CONFLICTO_HORARIOS);
        }

        if(repoPreInscripciones
                .existsPreinscripcionPendienteConIdMateria(materia.getIdMateria())) {
            throw new BusinessException(ErrorCode.PRE_INSCRIPCION_MATERIA_REPETIDA,
                    materia.getNombre());
        }

        Preinscripcion preinscripcion = new Preinscripcion();
        preinscripcion.setMateria(materia);
        preinscripcion.setAlumno(alumno);
        preinscripcion.setComisionDeseada(curso.getComision());

        repoPreInscripciones.save(preinscripcion);
    }

    @Transactional
    public void cerrarCursadaDe(Long idAlumno, Long idCurso) {
        Alumno alumno = alumnoService.obtenerAlumnoConId(idAlumno);
        Curso curso = cursoService.obtenerCursoConId(idCurso);

        curso.cerrarCursadaDe(alumno);
        cursoService.guardarCurso(curso);
    }

    @Transactional
    public Boolean cerroCursadaDe(Long idAlumno, Long idCurso) {
        Alumno alumno = alumnoService.obtenerAlumnoConId(idAlumno);
        Curso curso = cursoService.obtenerCursoConId(idCurso);

        return curso.cerroCursadaDe(alumno);
    }

    @Transactional
    public void matricularAAlumnoEnCurso(AlumnoFormDTO alumnoFormDTO, Long idCurso)  {
        Curso curso = cursoService.obtenerCursoConId(idCurso);
        Alumno alumno= alumnoService.obtenerAlumnoConDocumento(alumnoFormDTO.getDocumento());

        curso.inscribirAlumno(alumno);

        cursoService.guardarCurso(curso);
    }

    @Transactional
    public void darDeBajaAAlumnoDelCurso(Long idAlumno, Long idCurso)  {
        Alumno alumno= this.alumnoService.obtenerAlumnoConId(idAlumno);
        Curso curso = cursoService.obtenerCursoConId(idCurso);

        curso.darDeBajaA(alumno);
        cursoService.guardarCurso(curso);
    }

    @Transactional
    public List<PreinscripcionAlumnoDTO> obtenerCursosQuePuedePreinscribirseAlumnoParaMostrarConNombreMateria(
            Usuario usuarioAlumno,
            String nombreMateria
    ) {
        return this.obtenerMateriasQuePuedePreinscribirseAlumnoParaMostrar(usuarioAlumno).stream()
                .filter(dto -> dto.getNombreMateria().toLowerCase()
                        .contains(nombreMateria.toLowerCase()))
                .toList();
    }

    @Transactional
    public List<PreinscripcionAlumnoDTO> obtenerPreinscripcionesDe(Usuario usuarioAlumno) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuarioAlumno);

        return repoPreInscripciones.findAllByIdAlumno(alumno.getIdPersona()).stream()
                .map(this::transformarPreinscripcionAPreinscripcionAlumnoDTO)
                .toList();
    }


    public void eliminarPreinscripcionPendiente(Long idPreinscripcion, Usuario usuarioAlumno) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuarioAlumno);

        Preinscripcion preinscripcion = repoPreInscripciones.findById(idPreinscripcion)
                .orElseThrow(()-> new BusinessException(ErrorCode.PRE_INSCRIPCION_NO_EXISTE));

        if(!preinscripcion.esDeAlumno(alumno) || !preinscripcion.estaPendiente() ){
            throw new BusinessException(ErrorCode.PRE_INSCRIPCION_ELIMINAR_NO_PERMITIDO);
        }else {
            repoPreInscripciones.deleteById(idPreinscripcion);
        }
    }

    private PreinscripcionAlumnoDTO transformarAPreinscripcionAlumnoDTO(Curso curso){
        PreinscripcionAlumnoDTO dto = new PreinscripcionAlumnoDTO();

        Materia materia = curso.getMateria();

        dto.setComision(curso.getComision());
        dto.setIdCurso(curso.getIdCurso());
        dto.setNombreMateria(materia.getNombre());

        dto.setHorariosCursada(
                horariosMapper.tranformarAString(curso.getHorariosDeCurso())
        );

        return dto;
    }

    private PreinscripcionAlumnoDTO transformarPreinscripcionAPreinscripcionAlumnoDTO(
            Preinscripcion preinscripcion
    ){
        PreinscripcionAlumnoDTO dto = new PreinscripcionAlumnoDTO();

        dto.setComision(preinscripcion.getComisionDeseada());
        dto.setIdPreinscripcion(preinscripcion.getIdPreinscripcion());
        dto.setNombreMateria(preinscripcion.getMateria().getNombre());
        dto.setEstado(preinscripcion.getEstadoPreInscripcion().toString());

        dto.setHorariosCursada(
                horariosMapper.tranformarAString(
                        cursoService.obtenerCursoConComision(preinscripcion.getComisionDeseada())
                                .getHorariosDeCurso()
                )
        );

        return dto;
    }

    private boolean hayConflictoHorarios(List<HorarioCurso> horariosCursoQuePuedeAnotarse, Long idAlumno) {

        List<Preinscripcion> pendientes = repoPreInscripciones
                .findAllPendienteByIdAlumno(idAlumno);

        for (Preinscripcion preinscripcion : pendientes) {

            Curso cursoPreinscripto = cursoService.obtenerCursoConComision(
                    preinscripcion.getComisionDeseada()
            );

            List<HorarioCurso> horariosPreinscripto = cursoPreinscripto.getHorariosDeCurso();

            if (seSuperponenHorarios(horariosCursoQuePuedeAnotarse, horariosPreinscripto)) {
                return true;
            }
        }

        return false;
    }

    private boolean seSuperponenHorarios(List<HorarioCurso> horarios1, List<HorarioCurso> horarios2) {
        for (HorarioCurso a : horarios1) {
            for (HorarioCurso b : horarios2) {
                if (a.getDiaDeLaSemana().equals(b.getDiaDeLaSemana())) {

                    boolean solapa =
                            a.getHoraDesde() < b.getHoraHasta() &&
                                    b.getHoraDesde() < a.getHoraHasta();

                    if (solapa) return true;
                }
            }
        }
        return false;

    }


}
