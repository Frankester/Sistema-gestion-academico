package com.francocallero.sistema.de.gestion.academico.controllers.admin;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.carrera.CarreraDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.servicios.CarreraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/carreras")
@RequiredArgsConstructor
public class AdminCarrerasController extends ControllerBase {

    private final CarreraService carreraService;

    @GetMapping
    public String mostrarAdministrarCarreras(Model model){

        model.addAttribute("carreras",
                carreraService.obtenerTodasLasCarrerasParaMostrar());
        return "admin/carreras-administrar";
    }
    @GetMapping("/nuevo")
    public String mostrarFormNuevoCarrera(Model model){
        model.addAttribute("carreraForm", new CarreraDTO());

        return "admin/carrera-form";
    }

    @PostMapping("/nuevo")
    public String crearNuevaCarreraDesdeForm(@Valid @ModelAttribute("carreraForm") CarreraDTO form,
                                             BindingResult bindingResult,
                                             Model model,
                                             RedirectAttributes redirectAttributes){

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/carrera-form",
                redirectAttributes,
                "carrera.nuevo.success",
                "/admin/carreras",
                ()->
                        carreraService.crearCarrera(form)
        );
    }


    @GetMapping("/eliminar/{idCarrera}")
    public String eliminarCarrera(@PathVariable Long idCarrera,
                                  RedirectAttributes redirectAttributes){


        return this.manejarBusinessExceptionConRedirect(()->{
                    carreraService.eliminarCarrera(idCarrera);

                    return this.redirectSuccess(redirectAttributes,
                            "carrera.eliminar.success",
                            "/admin/carreras");
                },"/admin/carreras",
                redirectAttributes);
    }

    @GetMapping("/editar/{idCarrera}")
    public String mostrarFormParaEditarLaCarrera(@PathVariable Long idCarrera,
                                           Model model){

        this.indicarQueEsEditar(model);

        model.addAttribute("carreraForm",
                carreraService.obtenerParaMostrar(idCarrera));

        return "admin/carrera-form";
    }

    @PostMapping("/editar/{idCarrera}")
    public String editarLaCarreraDesdeForm(@PathVariable Long idCarrera,
                                           @Valid @ModelAttribute("carreraForm") CarreraDTO form,
                                           BindingResult bindingResult,
                                           Model model,
                                           RedirectAttributes redirectAttributes){

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/carrera-form",
                redirectAttributes,
                "carrera.editar.success",
                "/admin/carreras",
                ()->
                        carreraService.editarCarrera(form)
        );
    }

}
