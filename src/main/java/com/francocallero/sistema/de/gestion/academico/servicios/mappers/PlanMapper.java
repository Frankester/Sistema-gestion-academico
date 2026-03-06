package com.francocallero.sistema.de.gestion.academico.servicios.mappers;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.AnioMateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.MateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.PlanListDTO;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanMapper {

    public PlanListDTO transformarAPlanList(Plan plan){
        PlanListDTO dto = new PlanListDTO();

        this.mapPlanListDTO(dto, plan);
        Map<Integer,List<Materia>> materiasPorAnio = this.obtenerMapDeMateriasDePlan(plan);

        Set<Integer> anios = materiasPorAnio.keySet();

        dto.setAnios(
                anios.stream()
                        .map(anio -> this.tranformarAAnioMateriaDTO(materiasPorAnio.get(anio), anio))
                        .toList()
        );
        return dto;
    }

    public AnioMateriaDTO tranformarAAnioMateriaDTO(List<Materia> materias, Integer anio) {
        AnioMateriaDTO dto = new AnioMateriaDTO();
        dto.setAnio(anio);
        dto.setMaterias(
                materias.stream()
                        .map(this::transformarAMateriaDTO)
                        .toList()
        );

        return dto;
    }

    public MateriaDTO transformarAMateriaDTO(Materia materia){
        MateriaDTO dto = new MateriaDTO();
        this.mapMateriaDTO(dto, materia);

        return dto;
    }


    public PlanListDTO transformarAPlanListParaAlumno(Plan plan, Alumno alumno) {
        PlanListDTO dto = new PlanListDTO();

        this.mapPlanListDTO(dto,plan);

        Map<Integer,List<Materia>> materiasPorAnio = this.obtenerMapDeMateriasDePlan(plan);

        Set<Integer> anios = materiasPorAnio.keySet();

        dto.setAnios(
                anios.stream()
                        .map(anio -> this.tranformarAAnioMateriaDTOParaAlumno(materiasPorAnio.get(anio), anio, alumno))
                        .toList()
        );

        return dto;
    }

    private AnioMateriaDTO tranformarAAnioMateriaDTOParaAlumno(List<Materia> materias, Integer anio, Alumno alumno) {
        AnioMateriaDTO dto = new AnioMateriaDTO();
        dto.setAnio(anio);
        dto.setMaterias(
                materias.stream()
                        .map((materia)->this.transformarAMateriaDTOParaAlumno(materia, alumno))
                        .toList()
        );

        return dto;
    }

    private MateriaDTO transformarAMateriaDTOParaAlumno(Materia materia, Alumno alumno) {
        MateriaDTO dto = new MateriaDTO();
        this.mapMateriaDTO(dto, materia);

        dto.setCurso(
                alumno.cursoMateria(materia)
        );

        dto.setAprobo(
                alumno.cursoMateria(materia) &&
                        alumno.terminoDeCursar(materia) &&
                        alumno.aproboMateria(materia)
        );

        return dto;
    }

    private void mapMateriaDTO(MateriaDTO dto, Materia materia){
        dto.setIdMateria(materia.getIdMateria());
        dto.setNombreMateria(materia.getNombre());
    }
    private void mapPlanListDTO(PlanListDTO dto, Plan plan){

        dto.setCarrera(
                plan.getCarrera().getNombreCarrera()
        );
        dto.setNombre(plan.getNombre());
        dto.setIdPlan(plan.getIdPlan());
    }

    private  Map<Integer,List<Materia>> obtenerMapDeMateriasDePlan(Plan plan){
        List<Materia> materias = plan.getMaterias();//fetch materias from db

        return materias.stream()
                .collect(Collectors.groupingBy((Materia::getAnioPerteneciente)));
    }
}
