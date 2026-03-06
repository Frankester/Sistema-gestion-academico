package com.francocallero.sistema.de.gestion.academico.controllers.utils;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.persona.DatosPersonalesDTO;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.PersonaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class AbstractDatosPersonalesController extends ControllerBase {

    protected final PersonaService personaService;

    protected AbstractDatosPersonalesController(PersonaService personaService) {
        this.personaService = personaService;
    }

    protected abstract String getViewForm();

    protected abstract String getRedirectSuccessUrl();

    @GetMapping
    public String getDatosPersonales(Model model,
                                     @AuthenticationPrincipal Usuario usuario) {

        return this.manejarBusinessExceptionSinRedirect(() -> {

            DatosPersonalesDTO dto =
                    personaService.obtenerDatosPersonalesParaMostrar(usuario);

            model.addAttribute("datosPersonalesForm", dto);

            return getViewForm();

        }, getViewForm(), model, null);
    }

    @PostMapping
    public String guardarDatosPersonales(Model model,
                                         @AuthenticationPrincipal Usuario usuario,
                                         @Valid @ModelAttribute("datosPersonalesForm")
                                         DatosPersonalesDTO form,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {

        return this.manejarFormConRedirectEnExito(
                bindingResult,
                model,
                getViewForm(),
                redirectAttributes,
                "datos-personales.guardado-exito",
                getRedirectSuccessUrl(),
                () -> personaService.actualizarDatosPersonales(form, usuario)
        );
    }
}