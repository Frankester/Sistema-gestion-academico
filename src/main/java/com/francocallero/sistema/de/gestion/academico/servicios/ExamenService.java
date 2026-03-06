package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.NotaExamenFormDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.Examen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoExamenes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExamenService {

    private final InstanciaExamenService instanciaExamenService;
    private final AlumnoService alumnoService;
    private final RepoExamenes repoExamenes;

    public NotaExamenFormDTO crearNotaExamenForm(Long idInstanciaExamen, Long idAlumno) {
        NotaExamenFormDTO notaExamenFormDTO=new NotaExamenFormDTO();
        notaExamenFormDTO.setIdInstanciaExamen(idInstanciaExamen);
        notaExamenFormDTO.setIdAlumno(idAlumno);

        return notaExamenFormDTO;
    }

    @Transactional
    public NotaExamenFormDTO obtenerNotaExamenFormParaMostrar(Long idInstanciaExamen,
                                                              Long idAlumno){
        instanciaExamenService.validarSiExisteInstancia(idInstanciaExamen);
        Alumno alumno = alumnoService.obtenerAlumnoConId(idAlumno);

        NotaExamenFormDTO form = this.crearNotaExamenForm(idInstanciaExamen, idAlumno);

        Integer nota = instanciaExamenService.obtenerNotaDeAlumnoenInstancia(
                idInstanciaExamen,
                alumno);

        if(Objects.isNull(nota)){
            throw new BusinessException(ErrorCode.ALUMNO_EXAMEN_NO_TIENE_NOTA);
        }

        form.setNota(nota);

        return form;
    }

    public boolean aproboAlumnoExamenDeMateria(Long idAlumno, Materia materia) {
        List<Examen> examenes =  repoExamenes.findAllExamenByIdAlumnoAndMateriaId(
                idAlumno,
                materia.getIdMateria());

        return examenes.stream()
                .anyMatch((examen)->examen.tieneNota() && examen.aproboExamen());
    }

    public List<InstanciaExamen> obtenerInstanciasExamenesInscripto(Long idAlumno) {
        alumnoService.verificarSiExisteAlumnoConId(idAlumno);

        return repoExamenes.findAllInscriptoByIdAlumno(idAlumno);
    }
}