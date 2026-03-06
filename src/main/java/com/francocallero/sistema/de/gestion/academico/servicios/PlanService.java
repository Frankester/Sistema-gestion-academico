package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.PlanFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.PlanListDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Carrera;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoPlan;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.PlanMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PlanService {

    private final RepoPlan repoPlan;

    private final PlanMapper mapper;
    private final CarreraService carreraService;

    @Transactional
    public List<PlanListDTO> obtenerTodosParaMostrar(){
        List<Plan> planes = repoPlan.findAllByVirgenteTrue();

        return planes.stream()
                .map(mapper::transformarAPlanList)
                .toList();
    }

    @Transactional
    public PlanListDTO obtenerPlanParaMostrar(Long idPlan) {
        Plan plan = this.obtenerPlan(idPlan);

        return mapper.transformarAPlanList(plan);
    }

    public Plan obtenerPlan(Long idPlan) {
        return repoPlan.findById(idPlan)
                .orElseThrow(()->new BusinessException(ErrorCode.PLAN_NO_EXISTE));
    }

    @Transactional
    public void eliminarPlan(Long idPlan) {

        Plan plan = this.obtenerPlan(idPlan);
        plan.setVirgente(false);

        this.save(plan);
    }

    public void save(Plan plan) {
        this.repoPlan.save(plan);
    }



    public String obtenerNombreDePlan(Long idPlan) {
        Plan plan = this.obtenerPlan(idPlan);
        return plan.getNombre();
    }

    public void crearPlan(PlanFormDTO form) {
        if(repoPlan.existsByNombreAndIdCarrera(form.getNombre(),form.getIdCarrera())){
            throw new BusinessException(ErrorCode.PLAN_YA_EXISTE, form.getNombre());
        }else {
            Plan nuevoPlan = new Plan();
            nuevoPlan.setCarrera(
                    carreraService.obtenerCarreraConId(form.getIdCarrera()));
            nuevoPlan.setNombre(form.getNombre());

            this.save(nuevoPlan);
        }
    }
    @Transactional
    public List<PlanListDTO> obtenerTodosLosPlanesDeCarrera(Long idCarrera) {
        Carrera carrera = carreraService.obtenerCarreraConId(idCarrera);

        return this.obtenerTodosParaMostrar().stream()
                .filter(plan-> plan.getCarrera().equals(carrera.getNombreCarrera()))
                .toList();
    }





}
