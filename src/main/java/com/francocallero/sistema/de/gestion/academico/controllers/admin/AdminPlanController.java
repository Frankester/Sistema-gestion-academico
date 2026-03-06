package com.francocallero.sistema.de.gestion.academico.controllers.admin;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.MateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.PlanFormDTO;
import com.francocallero.sistema.de.gestion.academico.servicios.CarreraService;
import com.francocallero.sistema.de.gestion.academico.servicios.GestionPlanUsuarioService;
import com.francocallero.sistema.de.gestion.academico.servicios.MateriaService;
import com.francocallero.sistema.de.gestion.academico.servicios.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/planes")
@RequiredArgsConstructor
public class AdminPlanController extends ControllerBase {

    private final PlanService planService;
    private final MateriaService materiaService;
    private final CarreraService carreraService;
    private final GestionPlanUsuarioService gestionPlanUsuarioService;

    @GetMapping
    public String obtenerPlanes(@RequestParam(required = false) Long carreraId,
                                Model model){

        if (carreraId != null) {
            model.addAttribute("planes",
                    planService.obtenerTodosLosPlanesDeCarrera(carreraId));
        } else {
            model.addAttribute("planes",
                    planService.obtenerTodosParaMostrar());
        }

        this.cargarTodasLasCarrerasParaMostrar(model);

        model.addAttribute("carreraSeleccionada", carreraId);

        return "admin/planes-administrar";
    }

    @GetMapping("/nuevo")
    public String obtenerFormNuevoPlan(Model model){

        this.cargarTodasLasCarrerasParaMostrar(model);
        model.addAttribute("planForm", new PlanFormDTO());

        return "admin/plan-form";
    }

    @PostMapping("/nuevo")
    public String crearNuevoPlanDesdeForm(@Valid @ModelAttribute("planForm") PlanFormDTO planForm,
                                          BindingResult bindingResult,
                                          Model model,
                                          RedirectAttributes redirectAttributes){

        this.cargarTodasLasCarrerasParaMostrar(model);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/plan-form",
                redirectAttributes,
                "plan.nuevo.success",
                "/admin/planes",
                ()->
                        planService.crearPlan(planForm)
        );
    }


    @GetMapping("/{idPlan}")
    public String mostrarDetallePlan(@PathVariable Long idPlan,
                                     Model model){

        model.addAttribute("plan", planService.obtenerPlanParaMostrar(idPlan));

        return "admin/plan-detalle";
    }

    @GetMapping("/eliminar/{idPlan}")
    public String eliminarPlan(@PathVariable Long idPlan,
                               Model model,
                               RedirectAttributes redirectAttributes){

        return manejarBusinessExceptionSinRedirect(()->{
            planService.eliminarPlan(idPlan);

            return this.redirectSuccess(redirectAttributes,
                    "plan.eliminar.success","/admin/planes");
        },"admin/planes-administrar",
                model,
                null);
    }


    @GetMapping("/{idPlan}/materias/nueva")
    public String obtenerFormNuevaMateria(Model model,
                                          @PathVariable Long idPlan){
        model.addAttribute("materiaForm", new MateriaDTO());
        this.cargarIdYMateriasYNombrePlan(model, idPlan);
        this.cargarTodasLasCarrerasParaMostrar(model);

        return "admin/materia-form";
    }

    @PostMapping("/{idPlan}/materias/nueva")
    public String crearNuevaMateriaDesdeForm(Model model,
                                             @PathVariable Long idPlan,
                                             @Valid @ModelAttribute("materiaForm") MateriaDTO materiaDTO,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes
                                             ){
        this.cargarIdYMateriasYNombrePlan(model, idPlan);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/materia-form",
                redirectAttributes,
                "materia.nueva.success",
                "/admin/planes/"+idPlan,
                ()->
                        materiaService.crearMateria(idPlan,materiaDTO)
        );
    }

    @PostMapping("/{idPlan}/materias/{idMateria}/eliminar")
    public String eliminarMateria(@PathVariable Long idPlan,
                                  @PathVariable Long idMateria,
                                  RedirectAttributes redirectAttributes){
        return this.manejarBusinessExceptionConRedirect(()->{
            materiaService.eliminarMateriaDelPlan(idMateria, idPlan);

            return this.redirectSuccess(redirectAttributes,
                    "materia.eliminar.success", "/admin/planes/"+idPlan);
        },"/admin/planes/"+idPlan,
                redirectAttributes);
    }

    //UTILS
    private void cargarTodasLasCarrerasParaMostrar(Model model){
        model.addAttribute("carreras",
                carreraService.obtenerTodasLasCarrerasParaMostrar());
    }

    private void cargarIdYMateriasYNombrePlan(Model model, Long idPlan){
        model.addAttribute("idPlan", idPlan);
        model.addAttribute("materias", gestionPlanUsuarioService.obtenerMateriasDePlanParaMostrar(idPlan));
        model.addAttribute("planNombre",planService.obtenerNombreDePlan(idPlan));

    }

}
