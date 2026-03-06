package com.francocallero.sistema.de.gestion.academico.servicios.mappers;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoNotaExamenListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenListadoAlumnosDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenListadoDocentesDTO;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstanciaExamenMapper {


    public AlumnoNotaExamenListDTO transformarAlumnoAAlumnoNotaList(Alumno alumno,
                                                                    Integer nota){
        AlumnoNotaExamenListDTO dto = new AlumnoNotaExamenListDTO();

        dto.setNombreYApellido(alumno.getNombreYApellido());
        dto.setIdPersona(alumno.getIdPersona());
        dto.setNotaExamen(nota);

        return dto;
    }
    public ExamenListadoAlumnosDTO transformarAExamenListadoAlumnos(InstanciaExamen instanciaExamen, Integer cantAlumnos){
        ExamenListadoAlumnosDTO dto = new ExamenListadoAlumnosDTO();

        ExamenListadoDocentesDTO dtoDocentes =this.obtenerIdInstanciaFechaYNombreMateriaParaMostrarDe(
                instanciaExamen
        );//evitamos repetir logica

        dto.setIdExamen(dtoDocentes.getIdExamen());
        dto.setFecha(dtoDocentes.getFecha());
        dto.setMateriaNombre(dtoDocentes.getMateriaNombre());

        dto.setCantidadAlumnos(cantAlumnos);

        return dto;
    }

    public ExamenListadoDocentesDTO transformarAExamenListado(InstanciaExamen instanciaExamen) {
        ExamenListadoDocentesDTO dto = this.obtenerIdInstanciaFechaYNombreMateriaParaMostrarDe(instanciaExamen);

        List<Docente> docentes = instanciaExamen.getDocentes();//fetch to db exam's professors

        String docentesNombres = this.transformNombresDePersonasAString(docentes);

        dto.setDocentes(docentesNombres);
        dto.setSede(instanciaExamen.getSede());

        return dto;
    }

    public String transformNombresDePersonasAString(List<? extends Persona> personas){

        return personas.stream()
                .map(Persona::getNombreYApellido)
                .collect(Collectors.joining(",\n"));
    }

    public ExamenFormDTO transformarAExamenForm(InstanciaExamen instanciaExamen){
        ExamenFormDTO formDTO = new ExamenFormDTO();

        List<Docente> docentes = instanciaExamen.getDocentes();//fetch exam's professors from db
        Materia materia = instanciaExamen.getMateria();//fetch materia from db

        formDTO.setFecha(instanciaExamen.getFechaHora());
        formDTO.setMateriaId(materia.getIdMateria());

        List<Long> docentesId = docentes.stream()
                .map(Persona::getIdPersona)
                .toList();
        formDTO.setIdDocentes(docentesId);
        formDTO.setSede(instanciaExamen.getSede());

        return formDTO;
    }

    private ExamenListadoDocentesDTO obtenerIdInstanciaFechaYNombreMateriaParaMostrarDe(
            InstanciaExamen instanciaExamen
    ){
        ExamenListadoDocentesDTO dto = new ExamenListadoDocentesDTO();

        dto.setIdExamen(instanciaExamen.getIdInstanciaExamen());
        dto.setFecha(instanciaExamen.getFechaHora());
        dto.setMateriaNombre(instanciaExamen.getMateria().getNombre());
        return dto;
    }

}
