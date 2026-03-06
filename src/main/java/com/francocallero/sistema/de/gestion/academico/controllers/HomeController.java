package com.francocallero.sistema.de.gestion.academico.controllers;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class HomeController extends ControllerBase {


    private final UsuarioService usuarioService;


    @GetMapping("/")
    public String home(@AuthenticationPrincipal Usuario usuario, Model model){

        model.addAttribute("username", usuario.getUsername());
        String viewName = "index";
        return this.manejarBusinessExceptionSinRedirect(()->{
            Set<String> roles= usuarioService.obtenerRolesDe(usuario.getUsername());
            model.addAttribute("roles",roles);

            return viewName;
        },viewName,
                model,
                null);
    }
}
