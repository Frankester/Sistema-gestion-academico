package com.francocallero.sistema.de.gestion.academico.controllers;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.contrasenia.ContraseniaNuevaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.contrasenia.OlvidoContraseniaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.servicios.GestionUsuarioContrasenia;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContraseniasController extends ControllerBase {

    private final GestionUsuarioContrasenia gestionUsuarioContrasenia;

    private final String VIEW_OLVIDO_CONTRASENIA_FORM="contrasenias/olvido-contrasenia-form";
    private final String VIEW_CAMBIO_FORM="contrasenias/cambio-form";
    private final String VIEW_OLVIDO_CONTRASENIA_FORM_EXITO="contrasenias/olvido-contrasenia-form-exito";
    private final String VIEW_TOKEN_INVALIDO="contrasenias/token-invalido";

    @GetMapping("/contrasenias/olvido")
    public String mostrarFormOlvidoContrasenia(Model model){

        model.addAttribute("olvidoForm", new OlvidoContraseniaDTO());

        return VIEW_OLVIDO_CONTRASENIA_FORM;
    }

    @PostMapping("/contrasenias/olvido")
    public String gestionarOlvidoContrasenia(@Valid @ModelAttribute("olvidoForm") OlvidoContraseniaDTO form,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return VIEW_OLVIDO_CONTRASENIA_FORM;
        }

        return this.manejarBusinessExceptionConRedirect(()->{
                gestionUsuarioContrasenia.gestionarOlvidoContrasenia(form);

                return VIEW_OLVIDO_CONTRASENIA_FORM_EXITO;
            },"/login",
            redirectAttributes);
    }

    @GetMapping("/contrasenias/cambiar")
    public String verificarToken(@RequestParam("token") String token,
                                 Model model){

       return this.manejarBusinessExceptionSinRedirect(()->{
            Long idCambioContrasenia = gestionUsuarioContrasenia
                    .obtenerIdCambioContraseniaConToken(token);

            return "redirect:/contrasenias/"+idCambioContrasenia;
        },VIEW_TOKEN_INVALIDO,
                model,
                null);
    }

    @GetMapping("/contrasenias/{idCambioContrasenia}")
    public String mostrarFormCambioContrasenia(@PathVariable Long idCambioContrasenia,
                                               Model model){

        model.addAttribute("contraseniaForm", new ContraseniaNuevaDTO());
        model.addAttribute("idCambioContrasenia", idCambioContrasenia);

        return VIEW_CAMBIO_FORM;
    }

    @PostMapping("/contrasenias/{idCambioContrasenia}")
    public String cambiarContrasenia(@PathVariable Long idCambioContrasenia,
                                     @Valid @ModelAttribute("contraseniaForm")ContraseniaNuevaDTO form,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes){

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                VIEW_CAMBIO_FORM,
                redirectAttributes,
                "contrasenia.cambiada.exito",
                "/login",
                ()->gestionUsuarioContrasenia.cambiarContrasenia(idCambioContrasenia,form)
        );
    }


}
