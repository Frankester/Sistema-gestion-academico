package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.MateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.PlanListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.ResultadoComprobarMateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.TipoComprobacion;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoMaterias;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.PlanMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final RepoMaterias repoMaterias;
    private final PlanService planService;
    private final CarreraService carreraService;
    private final UsuarioService usuarioService;

    @Cacheable
    public List<Materia> findAll() {
        return repoMaterias.findAll();
    }

    public Materia save(Materia materia) {
       return this.repoMaterias.save(materia);
    }

    public Materia findById(Long idMateria) {
        return repoMaterias.findById(idMateria)
                .orElseThrow(()->new BusinessException(ErrorCode.MATERIA_NO_EXISTE));
    }

    @Transactional
    public void eliminarMateriaDelPlan(Long idMateria, Long idPlan) {
        Plan plan = planService.obtenerPlan(idPlan);
        Materia materia = this.findById(idMateria);
        plan.sacarMateria(materia);

        repoMaterias.eliminarReferenciasComoCorrelativa(idMateria);

        repoMaterias.delete(materia);
        planService.save(plan);
    }

    @Transactional
    public void crearMateria(Long idPlan, MateriaDTO dto) {
        Plan plan = planService.obtenerPlan(idPlan);

        if(plan.existeMateriaConNombre(dto.getNombreMateria())){
            throw new BusinessException(ErrorCode.PLAN_MATERIA_REPETIDA, dto.getNombreMateria());
        }

        Materia materia = new Materia();
        materia.setPlan(plan);
        materia.setNombre(dto.getNombreMateria());
        materia.setCarrera(
                carreraService.obtenerCarreraConId(dto.getIdCarrera())
        );
        materia.setAnioPerteneciente(dto.getAnioPerteneciente());

        dto.getIdsCorrelativas().stream()
                .map(this::findById)
                .forEach(materia::agregarCorrelativa);

        this.save(materia);
    }

    @Transactional
    public ResultadoComprobarMateriaDTO puedeCursarMateria(Long idMateria, Usuario usuario) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);
        Materia materia = this.findById(idMateria);

        if(alumno.cursoMateria(materia)){
            throw new BusinessException(ErrorCode.ALUMNO_MATERIA_YA_LA_CURSO, materia.getNombre());
        }

        return this.crearResultadoComprobarMateriaDTO(
                materia.cumpleConCorrelativasParaCursar(alumno),
                materia,
                materia.obtenerMateriasQueNecesitaParaCursar(alumno),
                TipoComprobacion.CURSAR
        );
    }

    @Transactional
    public ResultadoComprobarMateriaDTO puedeAprobarMateria(Long idMateria, Usuario usuario) {

        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);
        Materia materia = this.findById(idMateria);

        if(alumno.cursoMateria(materia) && alumno.terminoDeCursar(materia) && alumno.aproboMateria(materia)){
            throw new BusinessException(ErrorCode.ALUMNO_MATERIA_YA_LA_APROBO, materia.getNombre());
        }

        return this.crearResultadoComprobarMateriaDTO(
                materia.cumpleConCorrelativasParaAprobar(alumno),
                materia,
                materia.obtenerMateriasQueNecesitaParaAprobar(alumno),
                TipoComprobacion.APROBAR
        );
    }

    private ResultadoComprobarMateriaDTO crearResultadoComprobarMateriaDTO(Boolean puede,
                                                                           Materia materia,
                                                                           List<Materia> materiasQueNecesita,
                                                                           TipoComprobacion tipo) {
        ResultadoComprobarMateriaDTO dto = new ResultadoComprobarMateriaDTO();
        dto.setResultadoMateriaId(materia.getIdMateria());
        dto.setPuede(puede);
        dto.setMateriasQueNecesita(
                materiasQueNecesita.stream()
                        .map(Materia::getNombre)
                        .collect(Collectors.joining(", "))
        );
        dto.setTipo(tipo);

        return dto;
    }



}
