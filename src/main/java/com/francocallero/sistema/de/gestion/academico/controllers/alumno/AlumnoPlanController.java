package com.francocallero.sistema.de.gestion.academico.controllers.alumno;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.plan.ResultadoComprobarMateriaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.GestionPlanUsuarioService;
import com.francocallero.sistema.de.gestion.academico.servicios.MateriaService;
import com.francocallero.sistema.de.gestion.academico.servicios.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Supplier;

@Controller
@RequestMapping("/alumno/plan")
@RequiredArgsConstructor
public class AlumnoPlanController extends ControllerBase {

    private final MateriaService materiaService;
    private final GestionPlanUsuarioService gestionPlanUsuarioService;

    private final String URL_PLAN = "/alumno/plan";

    @GetMapping
    public String mostrarPlanDetalla(@AuthenticationPrincipal Usuario usuario,
                                     Model model){

        model.addAttribute("plan", gestionPlanUsuarioService.obtenerPlanParaMostrarDe(usuario));

        return "alumno/plan-detalle";
    }

    @GetMapping("/materias/{idMateria}/puede-cursar")
    public String puedeCursarMateria(@AuthenticationPrincipal Usuario usuario,
                                     @PathVariable Long idMateria,
                                     RedirectAttributes redirectAttributes){

        return this.manejarComprobacionMateria(redirectAttributes,
                ()->materiaService.puedeCursarMateria(idMateria, usuario));
    }

    @GetMapping("/materias/{idMateria}/puede-aprobar")
    public String puedeAprobarMateria(@AuthenticationPrincipal Usuario usuario,
                                     @PathVariable Long idMateria,
                                     RedirectAttributes redirectAttributes){

        return this.manejarComprobacionMateria(redirectAttributes,
                ()->materiaService.puedeAprobarMateria(idMateria, usuario));
    }

    private String manejarComprobacionMateria(RedirectAttributes redirectAttributes,
                                              Supplier<ResultadoComprobarMateriaDTO> action){
        return this.manejarBusinessExceptionConRedirect(()->{
                    redirectAttributes.addFlashAttribute("materiaResultado",
                            action.get());

                    return "redirect:"+URL_PLAN;
                }, URL_PLAN,
                redirectAttributes);
    }

}
