package com.francocallero.sistema.de.gestion.academico.controllers.alumno;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.InstanciaExamenDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.GestionExamenInstanciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/alumno/examenes")
@RequiredArgsConstructor
public class AlumnoExamenesController extends ControllerBase {
    private final GestionExamenInstanciaService gestionExamenInstanciaService;

    private final String VIEW_INSCRIPCION_EXAMENES= "alumno/inscripcion-examen";

    private final String URL_EXAMENES= "/alumno/examenes";

    @GetMapping
    public String mostrarExamenes(@RequestParam(required = false) String search,
                                  @AuthenticationPrincipal Usuario usuario,
                                  Model model){

        List< InstanciaExamenDTO> instancias = this.manejarBusqueda(search,
                ()->gestionExamenInstanciaService.obtenerInstanciasExamenQuePuedeRendir(usuario),
                (nombreMateria)->gestionExamenInstanciaService
                        .obtenerInstanciasExamenQuePuedeRendirConNombreMateria(usuario, nombreMateria));

        model.addAttribute("instanciasExamenes",instancias);
        model.addAttribute("instanciasExamenesInscripto",
                gestionExamenInstanciaService.obtenerInstanciasExamenInscriptoParaMostrar(usuario));

        return VIEW_INSCRIPCION_EXAMENES;
    }

    @PostMapping("/inscripcion/{idInstanciaExamen}")
    public String inscribirExamen(@PathVariable Long idInstanciaExamen,
                                  @AuthenticationPrincipal Usuario usuario,
                                  RedirectAttributes redirectAttributes){

        return this.manejarBusinessExceptionConRedirect(()->{
            gestionExamenInstanciaService.inscribirAlumnoAInstanciaExamen(usuario, idInstanciaExamen);

            return this.redirectSuccess(redirectAttributes,
                    "alumno.examenes.inscripcion.exito",
                    URL_EXAMENES);
        }, URL_EXAMENES,
                redirectAttributes);
    }

    @PostMapping("/inscripcion/{idInstanciaExamen}/eliminar")
    public String eliminarInscripcionExamen(@PathVariable Long idInstanciaExamen,
                                            @AuthenticationPrincipal Usuario usuario,
                                            RedirectAttributes redirectAttributes){

        return this.manejarBusinessExceptionConRedirect(()->{
                    gestionExamenInstanciaService.eliminarInscripcionAExamen(usuario, idInstanciaExamen);

                    return this.redirectSuccess(redirectAttributes,
                            "alumno.examenes.inscripcion-eliminada.exito",
                            URL_EXAMENES);
                }, URL_EXAMENES,
                redirectAttributes);
    }

}
