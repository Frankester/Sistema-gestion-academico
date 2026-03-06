package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.MateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.PlanListDTO;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.PlanMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionPlanUsuarioService {

    private final PlanService planService;
    private final UsuarioService usuarioService;
    private final PlanMapper planMapper;

    @Transactional
    public PlanListDTO obtenerPlanParaMostrarDe(Usuario usuario) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);

        return planMapper.transformarAPlanListParaAlumno(alumno.getPlanInscripto(), alumno);
    }

    @Transactional
    public List<MateriaDTO> obtenerMateriasDePlanParaMostrar(Long idPlan) {
        Plan plan = this.planService.obtenerPlan(idPlan);

        return plan.getMaterias().stream()
                .map(planMapper::transformarAMateriaDTO)
                .toList();
    }

}
