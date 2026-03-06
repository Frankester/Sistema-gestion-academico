package com.francocallero.sistema.de.gestion.academico.controllers.admin;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenListadoDocentesDTO;
import com.francocallero.sistema.de.gestion.academico.servicios.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/examenes")
@RequiredArgsConstructor
public class AdminExamenController extends ControllerBase {

    private final MateriaService materiaService;
    private final DocenteService docenteService;
    private final InstanciaExamenService instanciaExamenService;

    private final String VIEW_FORM_EXAMEN = "admin/fechas-examen";
    private final String URL_EXAMENES = "/admin/examenes";
    private final String KEY_MESSAGE_SUCCESS="examen.success";


    @GetMapping
    public String mostarExamenes(Model model){

        List<ExamenListadoDocentesDTO> instancias = instanciaExamenService.obtenerInstanciasParaMostrar();

        model.addAttribute("instancias", instancias);
        return "admin/examenes-administrar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {

        this.cargarMateriasYDocentesAModel(model);
        model.addAttribute("examenForm", new ExamenFormDTO());

        return VIEW_FORM_EXAMEN;
    }

    @PostMapping("/nuevo")
    public String guardarFechaExamen(
            @Valid @ModelAttribute("examenForm") ExamenFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        this.cargarMateriasYDocentesAModel(model);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                VIEW_FORM_EXAMEN,
                redirectAttributes,
                KEY_MESSAGE_SUCCESS,
                URL_EXAMENES,
                ()->
                        instanciaExamenService.crearExamenInstanciaExamen(
                            form.getMateriaId(),
                            form.getFecha(),
                            form.getIdDocentes(),
                                form.getSede()
                        )
        );
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {

        return this.manejarBusinessExceptionConRedirect(()->{
            ExamenFormDTO form = instanciaExamenService.obtenerInstanciaParaEditar(id);

            model.addAttribute("examenForm", form);
            this.cargarMateriasYDocentesAModelParaEditar(model, id);

            return VIEW_FORM_EXAMEN;
        },URL_EXAMENES,
                redirectAttributes);
    }

    @PostMapping("/editar/{id}")
    public String guardarFechaExamenEditada(
            @PathVariable Long id,
            @Valid @ModelAttribute("examenForm") ExamenFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        this.cargarMateriasYDocentesAModelParaEditar(model, id);


        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                VIEW_FORM_EXAMEN,
                redirectAttributes,
                KEY_MESSAGE_SUCCESS,
                URL_EXAMENES,
                ()->
                        instanciaExamenService.editarInstanciaExamen(form,id)
        );
    }


    @PostMapping("/{idInstanciaExamen}/eliminar")
    public String eliminarInstanciaExamen(@PathVariable Long idInstanciaExamen,
                                          Model model,
                                          RedirectAttributes redirectAttributes){

        return this.manejarBusinessExceptionConRedirect(()->{
                    instanciaExamenService.eliminarInstanciaExamen(idInstanciaExamen);

                    return this.redirectSuccess(redirectAttributes,
                            "examen.eliminado",
                            URL_EXAMENES);
        },URL_EXAMENES,
                redirectAttributes);
    }


    //UTILS
    private void cargarMateriasYDocentesAModelParaEditar(Model model, Long id){

        this.cargarMateriasYDocentesAModel(model);
        model.addAttribute("idExamen", id);
        this.indicarQueEsEditar(model);
    }


    private void cargarMateriasYDocentesAModel(Model model){
        model.addAttribute("materias", materiaService.findAll());
        model.addAttribute("docentes", docenteService.findAll());
    }


}

