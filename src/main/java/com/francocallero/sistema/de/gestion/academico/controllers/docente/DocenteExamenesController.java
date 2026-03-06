package com.francocallero.sistema.de.gestion.academico.controllers.docente;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoNotaExamenListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenListadoAlumnosDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.NotaExamenFormDTO;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.DocenteService;
import com.francocallero.sistema.de.gestion.academico.servicios.ExamenService;
import com.francocallero.sistema.de.gestion.academico.servicios.InstanciaExamenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/docente/examenes")
@RequiredArgsConstructor
public class DocenteExamenesController extends ControllerBase {

    private final DocenteService docenteService;

    private final ExamenService examenesService;
    private final InstanciaExamenService instanciaExamenService;

    @GetMapping
    public String getInstanciaExamenes(Model model,
                                       @AuthenticationPrincipal Usuario usuarioDocente){

        List<ExamenListadoAlumnosDTO> instanciasDeExamen = instanciaExamenService.getInstanciaExamenesDe(usuarioDocente);

        model.addAttribute("examenes",instanciasDeExamen);

        return "docente/examenes-administrar";
    }

    @GetMapping("/{idInstanciaExamen}/alumnos")
    public String getAlumnosDeInstanciaExamen(@AuthenticationPrincipal Usuario usuarioDocente,
                                              @PathVariable Long idInstanciaExamen,
                                              @RequestParam(required = false) String search,
                                              Model model,
                                              RedirectAttributes redirectAttributes){
        return this.manejarBusinessExceptionConRedirect(()->{
            List<AlumnoNotaExamenListDTO> alumnos= this.manejarBusqueda(search,
                    ()->instanciaExamenService.getAlumnosDeInstanciaExamen(
                            usuarioDocente,
                            idInstanciaExamen
                    ),
                    (busqueda)-> instanciaExamenService.buscarAlumnosDeInstanciaExamen(
                            usuarioDocente,
                            busqueda,
                            idInstanciaExamen
                    )
            );

            model.addAttribute("idInstanciaExamen", idInstanciaExamen);
            model.addAttribute("alumnos",alumnos);

            return "docente/examenes-alumnos-administrar";
        },"/docente/examenes",
                redirectAttributes);
    }

    @GetMapping("/{idInstanciaExamen}/alumnos/{idAlumno}/nota/nuevo")
    public String getFormNotasNuevo(Model model,
                                    @PathVariable Long idInstanciaExamen,
                                    @PathVariable Long idAlumno){

        NotaExamenFormDTO notaExamenFormDTO = examenesService.crearNotaExamenForm(idInstanciaExamen, idAlumno);

        this.cargarIdAlumnoYIdInstanciaExamen(model, idInstanciaExamen, idAlumno);
        model.addAttribute("notaExamenForm",notaExamenFormDTO);

        return "docente/examenes-nota-form";
    }

    @PostMapping("/{idInstanciaExamen}/alumnos/{idAlumno}/nota/nuevo")
    public String subirNotaDelExamenDelAlumno(Model model,
                                  @AuthenticationPrincipal Usuario usuarioDocente,
                                  @PathVariable Long idInstanciaExamen,
                                  @PathVariable Long idAlumno,
                                  @Valid @ModelAttribute("notaExamenForm")NotaExamenFormDTO form,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes){

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "docente/examenes-nota-form",
                redirectAttributes,
                "nota.examen.success",
                "/docente/examenes/"+idInstanciaExamen+"/alumnos",
                ()->{
                    form.setIdAlumno(idAlumno);
                    docenteService.subirNotaExamen(usuarioDocente,form);
                });
    }


    @GetMapping("/{idInstanciaExamen}/alumnos/{idAlumno}/nota/edit")
    public String getEditarNota(Model model,
                                @AuthenticationPrincipal Usuario usuarioDocente,
                                @PathVariable Long idInstanciaExamen,
                                @PathVariable Long idAlumno,
                                RedirectAttributes redirectAttributes){
        this.cargarIdAlumnoYIdInstanciaExamen(model, idInstanciaExamen, idAlumno);
        this.indicarQueEsEditar(model);

        return this.manejarBusinessExceptionConRedirect(()->{
            NotaExamenFormDTO notaExamenFormDTO = examenesService.obtenerNotaExamenFormParaMostrar(
                    idInstanciaExamen,
                    idAlumno);
            model.addAttribute("notaExamenForm",notaExamenFormDTO);

            return "docente/examenes-nota-form";
        },"/docente/examenes/"+idInstanciaExamen+"/alumnos",
                redirectAttributes);
    }

    //UTILS

    private void cargarIdAlumnoYIdInstanciaExamen(Model model, Long idInstancia, Long idAlumno){
        model.addAttribute("idInstanciaExamen", idInstancia);
        model.addAttribute("idAlumno", idAlumno);
    }

}
