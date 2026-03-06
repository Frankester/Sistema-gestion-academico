package com.francocallero.sistema.de.gestion.academico.controllers.admin;


import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.CursoFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.HorarioCursoDTO;
import com.francocallero.sistema.de.gestion.academico.servicios.DocenteService;
import com.francocallero.sistema.de.gestion.academico.servicios.CursoService;
import com.francocallero.sistema.de.gestion.academico.servicios.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cursos")
@RequiredArgsConstructor
public class AdminCursoController extends ControllerBase {

    private final CursoService cursoService;
    private final MateriaService materiaService;
    private final DocenteService docenteService;

    @GetMapping
    public String listarCursos(Model model) {

        this.cargarTodosLosCursosParaMostrar(model);
        return "admin/cursos-administrar";
    }

    @GetMapping("/{comision}")
    public String mostrarCurso(@PathVariable String comision,
                               Model model){

        model.addAttribute("curso", cursoService.obtenerParaMostrarConComision(comision));

        return "admin/curso-detalle";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        CursoFormDTO cursoForm = new CursoFormDTO();
        HorarioCursoDTO horarioNuevo = new HorarioCursoDTO();

        cursoForm.agregarHorario(horarioNuevo);

        this.cargarCursoForm(model,cursoForm);
        this.cargarMateriasYDocentes(model);

        return "admin/curso-form";
    }

    @PostMapping(value="/nuevo", params = "addHorario")
    public String agregarHorario(@ModelAttribute CursoFormDTO form, Model model){

        HorarioCursoDTO horarioNuevo = new HorarioCursoDTO();

        form.agregarHorario(horarioNuevo);

        this.cargarCursoForm(model, form);
        this.cargarMateriasYDocentes(model);

        return "admin/curso-form";
    }

    @PostMapping(value="/editar/{id}", params = "addHorario")
    public String editarAgregarHorario(@PathVariable Long id,
                                       @ModelAttribute("cursoForm") CursoFormDTO form,
                                       Model model){
        form.agregarHorario(new HorarioCursoDTO());

        this.cargarMateriasYDocentes(model);
        this.cargarIdYEditar(model,id);

        return "admin/curso-form";
    }



    @PostMapping(value="/editar/{idCurso}",  params = "removeHorario")
    public String eliminarHorario(@ModelAttribute CursoFormDTO form,
                                  Model model,
                                  @PathVariable Long idCurso,
                                  @RequestParam Long removeHorario){

        form.eliminarHorarioconIndice(removeHorario.intValue());

        this.cargarCursoForm(model, form);
        this.cargarMateriasYDocentes(model);
        this.cargarIdYEditar(model,idCurso);

        return "admin/curso-form";
    }

    @PostMapping(value="/nuevo", params = "removeHorario")
    public String eliminarHorario(@ModelAttribute CursoFormDTO form,
                                  Model model,
                                  @RequestParam Long removeHorario){
        form.eliminarHorarioconIndice(removeHorario.intValue());

        this.cargarCursoForm(model, form);
        this.cargarMateriasYDocentes(model);

        return "admin/curso-form";
    }

    @PostMapping("/nuevo")
    public String crearCurso(
            @Valid @ModelAttribute("cursoForm") CursoFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        this.cargarMateriasYDocentes(model);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/curso-form",
                redirectAttributes,
                "curso.creado",
                "/admin/cursos",
                ()->
                        cursoService.crearDesdeForm(form)
                );
    }

    @GetMapping("/editar/{id}")
    public String editarCurso(@PathVariable Long id, Model model) {


        this.cargarMateriasYDocentes(model);
        this.cargarIdYEditar(model,id);


        return this.manejarBusinessExceptionSinRedirect(()->{
            CursoFormDTO cursoFormDTO = cursoService.obtenerParaEditar(id);

            this.cargarCursoForm(model, cursoFormDTO);

            return "admin/curso-form";
        },"admin/curso-form",
                model,
                null);

    }

    @PostMapping("/editar/{id}")
    public String actualizarCurso(
            @PathVariable Long id,
            @Valid @ModelAttribute("cursoForm") CursoFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        this.cargarMateriasYDocentes(model);
        this.cargarIdYEditar(model, id);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "admin/curso-form",
                redirectAttributes,
                "curso.actualizado",
                "/admin/cursos",
                ()->
                        cursoService.actualizarCurso(id, form)
        );

    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCurso(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

       this.cargarTodosLosCursosParaMostrar(model);

        return this.manejarBusinessExceptionSinRedirect(()->{
                    cursoService.eliminarCurso(id);

                    return this.redirectSuccess(redirectAttributes,
                            "curso.eliminado",
                            "/admin/cursos");
                },"admin/cursos-administrar",
                model,
                null);
    }

    //UTILS
    private void cargarMateriasYDocentes(Model model){
        model.addAttribute("materias", materiaService.findAll());
        model.addAttribute("docentes",docenteService.findAll());
    }

    private void cargarIdYEditar(Model model, Long id){
        this.indicarQueEsEditar(model);
        model.addAttribute("idCurso",id);
    }

    private void cargarCursoForm(Model model, CursoFormDTO form){
        model.addAttribute("cursoForm", form);
    }

    private void cargarTodosLosCursosParaMostrar(Model model){
        model.addAttribute("cursos", cursoService.obtenerTodosParaMostrar());
    }

}

