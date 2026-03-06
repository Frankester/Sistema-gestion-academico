package com.francocallero.sistema.de.gestion.academico.controllers.alumno;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.inscripcion.PreinscripcionAlumnoDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.GestionCursoAlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/alumno/preinscripciones")
@RequiredArgsConstructor
public class AlumnoPreinscripcionController extends ControllerBase {

    private final GestionCursoAlumnoService gestionCursoAlumnoService;

    private final String VIEW_PREINSCIPCION="alumno/preinscripcion-cursos";
    private final String URL_PREINSCIPCION="/alumno/preinscripciones";

    @GetMapping
    public String mostrarMateriasQuePuedePreinscribirse(Model model,
                                                        @AuthenticationPrincipal Usuario usuarioAlumno,
                                                        @RequestParam(required = false) String search){
        model.addAttribute("search", search);

        return this.manejarBusinessExceptionSinRedirect(()->{
            List<PreinscripcionAlumnoDTO> cursosDtos = this.manejarBusqueda(search,
                    ()-> gestionCursoAlumnoService
                            .obtenerMateriasQuePuedePreinscribirseAlumnoParaMostrar((usuarioAlumno)),
                    (busquedaNombreMateria)-> gestionCursoAlumnoService
                            .obtenerCursosQuePuedePreinscribirseAlumnoParaMostrarConNombreMateria(
                                    usuarioAlumno,
                                    busquedaNombreMateria
                            ));

            model.addAttribute("cursos", cursosDtos);

            List<PreinscripcionAlumnoDTO> preinscripciones= gestionCursoAlumnoService.
                    obtenerPreinscripcionesDe(usuarioAlumno);

            model.addAttribute("preinscripciones", preinscripciones);

            return VIEW_PREINSCIPCION;
        },VIEW_PREINSCIPCION,
                model,
                null);
    }


    @PostMapping("{idCurso}")
    public String mostrarMateriasQuePuedePreinscribirse(@PathVariable Long idCurso,
                                                        Model model,
                                                        @AuthenticationPrincipal Usuario usuarioAlumno,
                                                        RedirectAttributes redirectAttributes){

        return this.manejarFormConRedirectEnExito(null,
                model,
                VIEW_PREINSCIPCION,
                redirectAttributes,
                "alumno.preinscipcion.exito",
                URL_PREINSCIPCION,
                ()-> gestionCursoAlumnoService.preinscribirAlumno(usuarioAlumno, idCurso)
                );
    }

    @PostMapping("{IdPreinscripcion}/eliminar")
    public String eliminarPreinscripcionPendiente(@PathVariable Long IdPreinscripcion,
                                                  @AuthenticationPrincipal Usuario usuarioAlumno,
                                                  RedirectAttributes redirectAttributes){
        return this.manejarBusinessExceptionConRedirect(()->{
            gestionCursoAlumnoService.eliminarPreinscripcionPendiente(
                    IdPreinscripcion,
                    usuarioAlumno
            );

            return this.redirectSuccess(redirectAttributes,
                    "alumno.preinscripcion.eliminada.exito",
                    URL_PREINSCIPCION);
        }, URL_PREINSCIPCION,
                redirectAttributes);
    }

}
