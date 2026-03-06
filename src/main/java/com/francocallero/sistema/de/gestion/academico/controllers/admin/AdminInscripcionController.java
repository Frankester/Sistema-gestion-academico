package com.francocallero.sistema.de.gestion.academico.controllers.admin;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.servicios.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/inscripciones")
@RequiredArgsConstructor
public class AdminInscripcionController extends ControllerBase {

    private final InscripcionService inscripcionService;

    @GetMapping
    public String mostrarListaPreinscripciones(Model model) {

       model.addAttribute("preinscripciones",
               inscripcionService.findAllPreinscripcionesPendientes());

        return "admin/inscripciones-administrar";
    }

    @GetMapping("{idPreinscripcion}/aceptar")
    public String aceptarPreinscripcion(@PathVariable Long idPreinscripcion,
                                        RedirectAttributes redirectAttributes) {

       return this.manejarBusinessExceptionConRedirect(()->{
           inscripcionService.inscribir(idPreinscripcion);

           return this.redirectSuccess(redirectAttributes,
                   "inscripcion.success",
                   "/admin/inscripciones");
       },"/admin/inscripciones",redirectAttributes);
    }

    @GetMapping("{idPreinscripcion}/rechazar")
    public String rechazarPreinscripcion(@PathVariable Long idPreinscripcion,
                                         RedirectAttributes redirectAttributes) {

        return this.manejarBusinessExceptionConRedirect(()->{
            inscripcionService.rechazarPreInscripcion(idPreinscripcion);

            return this.redirectSuccess(redirectAttributes,
                    "inscripcion.rechazo.success",
                    "/admin/inscripciones");
        },"/admin/inscripciones",redirectAttributes);
    }


}

