package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoNotaCursoListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.*;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.*;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoCursos;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.CursoMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CursoService {

    private final RepoCursos repoCursos;

    private final DocenteService docenteService;
    private final MateriaService materiaService;

    private final UsuarioService usuarioService;

    private final CursoMapper mapper;


    public Curso obtenerCursoConId(Long id) {
        return repoCursos.findById(id)
                .orElseThrow(()->new BusinessException(ErrorCode.CURSO_NO_VIRGENTE));
    }

    public void crearDesdeForm(CursoFormDTO form) {
        Curso nuevoCurso = new Curso();
        String comision = form.getComision();

        Optional<Curso> opCurso = repoCursos.findByComisionIgnoreCase(comision);

        if(opCurso.isPresent()){
            throw new BusinessException(ErrorCode.CURSO_EXISTENTE, comision);
        }else {
            actualizarCursoDeForm(form,  nuevoCurso);

            this.guardarCurso(nuevoCurso);
        }
    }

    public CursoFormDTO obtenerParaEditar(Long id) {

        Curso curso = this.obtenerCursoConId(id);

        return mapper.transformarACursoForm(curso);
    }

    public void actualizarCurso(Long idCurso, CursoFormDTO form) {
        Curso curso= this.obtenerCursoConId(idCurso);

        actualizarCursoDeForm(form, curso);

        curso.setIdCurso(idCurso);

        this.guardarCurso(curso);
    }

    public void eliminarCurso(Long idCurso) {
        Curso curso =  this.obtenerCursoConId(idCurso);
        curso.setVirgente(false);

        this.guardarCurso(curso);
    }

    public void guardarCurso(Curso curso) {
        repoCursos.save(curso);
    }

    @Transactional
    @Cacheable("cursosVirgentes")
    public List<CursoListDTO> obtenerTodosParaMostrar() {
       return repoCursos.findByVirgenteTrue().stream()
               .map(curso->mapper.transformToCursoList(curso,
                       repoCursos.countAlumnosByCurso(curso.getIdCurso()).intValue()
                       )).toList();
    }
    @Transactional
    @Cacheable
    public List<AlumnoNotaCursoListDTO> obtenerTodosAlumnosParaMostrar(Long idCurso)  {
        Curso curso = this.obtenerCursoConId(idCurso);
        return  curso.getAlumnosInscriptos().stream()
                .map((alumno)->mapper.transformarAAlumnoNotaCursoListDTO(alumno, curso))
                .toList();
    }


    public Curso obtenerCursoConComision(String comision) {
        return repoCursos.findByComisionIgnoreCase(comision)
                .orElseThrow(()->new BusinessException(ErrorCode.CURSO_NOT_FOUND));
    }

    @Transactional
    public CursoListDTO obtenerParaMostrarConComision(String comision) {
        Curso curso = this.obtenerCursoConComision(comision);
        return mapper.transformToCursoList(curso,
                repoCursos.countAlumnosByCurso(curso.getIdCurso()).intValue()
                );
    }

    @Transactional
    public List<AlumnoNotaCursoListDTO> obtenerAlumnosPorNombreOApellido(Long idCurso, String nombreOApellidoABuscar) {
        Curso curso = this.obtenerCursoConId(idCurso);
        return repoCursos
                .findByCursoAndNombreOrApellidoContainingIgnoreCase(idCurso, nombreOApellidoABuscar)
                .stream()
                .map((alumno)->mapper.transformarAAlumnoNotaCursoListDTO(alumno,curso))
                .toList();
    }

    public List<Curso> obtenerCursosDisponiblesDeMaterias(List<Materia> materiasQuePuedeInscribirse) {

        return materiasQuePuedeInscribirse.stream()
                .map(Materia::getIdMateria)
                .flatMap((materiaId)->repoCursos.findAllByMateriaIdAndVirgenteTrue(materiaId).stream())
                .toList();
    }


    private void verificarHoraDesdeHoraHastaCurso(Integer horaDesde, Integer horaHasta){
        if (horaDesde>=horaHasta){
            throw new BusinessException(ErrorCode.HORARIO_CURSADA_INVALIDO);
        }
    }
    private void actualizarCursoDeForm(CursoFormDTO form, Curso curso) {

        form.getHorariosCursada().forEach(h ->
                this.verificarHoraDesdeHoraHastaCurso(
                        h.getHoraDesde(),
                        h.getHoraHasta()
                )
        );

        curso.setComision(form.getComision());
        curso.setFechaInicioCurso(form.getFechaInicioCursada());
        curso.setDocente(docenteService.findById(form.getIdDocente()));
        curso.setMateria(materiaService.findById(form.getIdMateria()));

        this.actualizarHorariosDeCursoForm(form, curso);
    }

    private void actualizarHorariosDeCursoForm(CursoFormDTO form, Curso curso){
        //para busqueda mas rápida: O(1)
        Map<Long, HorarioCurso> horariosExistentes = curso.getHorariosDeCurso()
                .stream()
                .collect(Collectors.toMap(
                        HorarioCurso::getIdHorarioCurso,
                        Function.identity()
                ));

        //limpiamos los horarios viejos
        curso.getHorariosDeCurso().clear();

        for (HorarioCursoDTO dto : form.getHorariosCursada()) {

            if (dto.getIdHorario() != null) {
                // existente -> actualizar
                HorarioCurso existente = horariosExistentes.get(dto.getIdHorario());

                existente.setDiaDeLaSemana(dto.getDiaDeLaSemana());
                existente.setHoraDesde(dto.getHoraDesde());
                existente.setHoraHasta(dto.getHoraHasta());

                curso.agregarHorarioCursada(existente);

            } else {
                // nuevo -> crear
                curso.agregarHorarioCursada(
                        mapper.transformHorarioCursoDTOToHorarioCurso(dto)
                );
            }
        }
    }

    @Transactional
    public List<CursoAlumnoDTO> obtenerTodosParaMostrarAAlumno(Usuario usuario) {
        Alumno alumno = (Alumno) this.usuarioService.obtenerPersonaDeUsuario(usuario);

        return this.repoCursos.findAllByVirgenteTrue().stream()
                .filter(curso->curso.esAlumno(alumno))
                .map((curso)->mapper.transformarACursoAlumnoDTO(curso, alumno))
                .toList();
    }

    public List<CursoAlumnoDTO> obtenerTodosparaMostrarConNombre(String nombreMateria, Usuario usuario) {
        Alumno alumno = (Alumno) this.usuarioService.obtenerPersonaDeUsuario(usuario);

        return this.repoCursos.searchAllByVirgenteTrueAndMateriaNombre(nombreMateria).stream()
                .map(curso->mapper.transformarACursoAlumnoDTO(curso, alumno))
                .toList();
    }

    @Transactional
    public CursoAlumnoListDTO obtenerCursoConIdParaMostrar(Long idCurso, Usuario usuario) {
        Curso curso = this.obtenerCursoConId(idCurso);
        Alumno alumno = (Alumno) this.usuarioService.obtenerPersonaDeUsuario(usuario);

        return mapper.transformarACursoAlumnoListDTO(curso, alumno);
    }

    public void cerrarCurso(Long idCurso, Usuario usuario) {
        Docente docente = (Docente) usuarioService.obtenerPersonaDeUsuario(usuario);
        Curso curso = this.obtenerCursoConId(idCurso);

        if(!curso.esDocente(docente)){
            throw new BusinessException(ErrorCode.DOCENTE_CURSO_NO_PERTENECE);
        }

        curso.setVirgente(false);

        this.guardarCurso(curso);
    }
}
