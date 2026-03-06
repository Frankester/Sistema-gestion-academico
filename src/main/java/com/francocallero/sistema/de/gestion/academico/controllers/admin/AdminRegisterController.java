package com.francocallero.sistema.de.gestion.academico.controllers.admin;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.persona.UsuarioDTO;
import com.francocallero.sistema.de.gestion.academico.servicios.PlanService;
import com.francocallero.sistema.de.gestion.academico.servicios.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/register")
@RequiredArgsConstructor
public class AdminRegisterController extends ControllerBase {

    private final UsuarioService usuarioService;
    private final PlanService planService;

    @GetMapping
    public String register(Model model){
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("planes", planService.obtenerTodosParaMostrar());
        return "admin/register";
    }

    @PostMapping
    public String registrarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioDTO usuarioDTO,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes  redirectAttributes){

        model.addAttribute("planes", planService.obtenerTodosParaMostrar());

        return  this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/register",
                redirectAttributes,
                "register.success",
                "/admin/register",
                ()->
                        usuarioService.crearUserDeForm(usuarioDTO)
                );
    }

}
