package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion.PreinscripcionListAdminDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Preinscripcion;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoPreInscripciones;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final RepoPreInscripciones repoPreInscripciones;
    private final CursoService cursoService;


    @Transactional
    public List<PreinscripcionListAdminDTO> findAllPreinscripcionesPendientes(){
        List<Preinscripcion> preinscripciones=repoPreInscripciones.findAllPendientes();

        return preinscripciones.stream()
                .map(this::transformarAPreinscricionDTO)
                .toList();
    }


    public void rechazarPreInscripcion(Long idPreinscripcion){

        Preinscripcion preinscripcion = this.buscarPreinscripcionDe(idPreinscripcion);
        preinscripcion.rechazarPreInscripcion();
        repoPreInscripciones.save(preinscripcion);
    }

    @Transactional
    public void inscribir(Long idPreinscripcion) {
        Preinscripcion preinscripcion = this.buscarPreinscripcionDe(idPreinscripcion);
        Alumno alumno = preinscripcion.getAlumno();//fetch alumno from db

        Curso curso = cursoService.obtenerCursoConComision(preinscripcion.getComisionDeseada());
        curso.inscribirAlumno(alumno);

        preinscripcion.aceptarPreInscripcion();

        repoPreInscripciones.save(preinscripcion);
        cursoService.guardarCurso(curso);
    }

    private Preinscripcion buscarPreinscripcionDe(Long idPreinscripcion) {
        return repoPreInscripciones.findById(idPreinscripcion)
                .orElseThrow(()->new BusinessException(ErrorCode.PRE_INSCRIPCION_NO_EXISTE));
    }

    private PreinscripcionListAdminDTO transformarAPreinscricionDTO(Preinscripcion preinscripcion) {
        PreinscripcionListAdminDTO dto = new PreinscripcionListAdminDTO();
        dto.setComision(preinscripcion.getComisionDeseada());
        dto.setNombreApellidoAlumno(
                preinscripcion.getAlumno().getNombreYApellido()
        );
        dto.setNombreMateria(
                preinscripcion.getMateria().getNombre()
        );
        dto.setIdPreinscripcion(preinscripcion.getIdPreinscripcion());
        dto.setEstado(
                preinscripcion.getEstadoPreInscripcion().toString()
        );
        return dto;
    }

}
