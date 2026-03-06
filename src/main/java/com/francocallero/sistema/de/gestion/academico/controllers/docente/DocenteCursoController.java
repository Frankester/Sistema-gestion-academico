package com.francocallero.sistema.de.gestion.academico.controllers.docente;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoNotaCursoListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.NotaListDTO;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.AlumnoService;
import com.francocallero.sistema.de.gestion.academico.servicios.CursoService;
import com.francocallero.sistema.de.gestion.academico.servicios.GestionCursoAlumnoService;
import com.francocallero.sistema.de.gestion.academico.servicios.NotasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.rmi.server.UID;
import java.util.List;

@Controller
@RequestMapping("/docente/cursos")
@RequiredArgsConstructor
public class DocenteCursoController extends ControllerBase {

    private final CursoService cursoService;
    private final GestionCursoAlumnoService gestionCursoAlumnoService;
    private final NotasService notasService;
    private final AlumnoService alumnoService;


    @GetMapping
    public String getCursos(Model model){

        model.addAttribute("cursos", cursoService.obtenerTodosParaMostrar());

        return "docente/cursos-administrar";
    }


    @PostMapping("/{idCurso}/cerrar")
    public String cerrarCurso(@PathVariable Long idCurso,
                              @AuthenticationPrincipal Usuario usuario,
                              RedirectAttributes redirectAttributes){

        return this.manejarBusinessExceptionConRedirect(()->{
            cursoService.cerrarCurso(idCurso, usuario);

            return this.redirectSuccess(redirectAttributes,
                    "curso.cerro.exito",
                    "/docente/cursos");
        }, "/docente/cursos", redirectAttributes);
    }


    @GetMapping("/{idCurso}/alumnos")
    public String getAlumnos(@PathVariable Long idCurso,
                             @RequestParam(required = false) String search,
                             Model model,
                             RedirectAttributes redirectAttributes){

        this.cargarIdCurso(model, idCurso);
        model.addAttribute("search", search);

        return this.manejarBusinessExceptionConRedirect(()->{
            List<AlumnoNotaCursoListDTO> alumnos= this.manejarBusqueda(search,
                    ()->cursoService.obtenerTodosAlumnosParaMostrar(idCurso),
                    (busqueda)-> cursoService.obtenerAlumnosPorNombreOApellido(idCurso, busqueda)
            );

            model.addAttribute("alumnos", alumnos);

            return "docente/curso-alumnos-administrar";
        },
                "/docente/cursos",
                redirectAttributes);
    }


    @GetMapping("/{idCurso}/matricular")
    public String addAlumno(@PathVariable Long idCurso,
                            Model model){

        this.cargarIdCurso(model, idCurso);
        model.addAttribute("alumnoForm",new AlumnoFormDTO());


        return "docente/curso-alumno-matricular";
    }


    @PostMapping("/{idCurso}/matricular")
    public String addAlumnoDesdeForm(@PathVariable Long idCurso,
                            @Valid @ModelAttribute("alumnoForm") AlumnoFormDTO alumnoFormDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "docente/curso-alumno-matricular",
                redirectAttributes,
                "matricular.success",
                "/docente/cursos/"+idCurso+"/matricular",
                ()->
                        gestionCursoAlumnoService.matricularAAlumnoEnCurso(alumnoFormDTO,idCurso)
        );

    }

    @PostMapping("/{idCurso}/alumnos/{idAlumno}/eliminar")
    public String deleteAlumno(
            @PathVariable("idCurso") Long idCurso,
            @PathVariable("idAlumno") Long idAlumno,
            RedirectAttributes redirectAttributes){

        return this.manejarBusinessExceptionConRedirect(()->{
            gestionCursoAlumnoService.darDeBajaAAlumnoDelCurso(idAlumno, idCurso);

            return this.redirectSuccess(redirectAttributes,
                    "alumno.eliminar.success",
                    "/docente/cursos/"+ idCurso + "/alumnos");
        },"/docente/cursos/"+ idCurso + "/alumnos",redirectAttributes);
    }

    @GetMapping("/{idCurso}/alumnos/{idAlumno}/notas")
    public String getAlumnoNotas(@PathVariable("idCurso") Long idCurso,
                                 @PathVariable("idAlumno") Long idAlumno,
                                 Model model,
                                 RedirectAttributes redirectAttributes){

        this.cargarIdAlumnoYIdCurso(model, idAlumno, idCurso);

        return this.manejarBusinessExceptionConRedirect(()->{

                model.addAttribute("cerroCursada",
                        gestionCursoAlumnoService.cerroCursadaDe(idAlumno, idCurso));

                model.addAttribute("notas",
                        notasService.findAllNotasParaMostrarDeAlumno(idAlumno, idCurso));

                return "docente/curso-alumno-notas-administrar";


        },"/docente/cursos/"+idCurso+"/alumnos",
                redirectAttributes);

    }

    @PostMapping("/{idCurso}/alumnos/{idAlumno}/cerrar")
    public String cerrarCursada(@PathVariable Long idCurso,
                                @PathVariable Long idAlumno,
                                RedirectAttributes redirectAttributes){
        return this.manejarBusinessExceptionConRedirect(()->{
                    gestionCursoAlumnoService.cerrarCursadaDe(idAlumno, idCurso);

            return this.redirectSuccess(redirectAttributes,
                    "cursada.cerrada.exito",
                    "/docente/cursos/"+idCurso+"/alumnos/"+idAlumno+"/notas");

        }, "/docente/cursos/"+idCurso+"/alumnos",
                redirectAttributes);
    }

    @GetMapping("/{idCurso}/alumnos/{idAlumno}/notas/nuevo")
    public String addNotaAlumno(@PathVariable("idCurso") Long idCurso,
                                 @PathVariable("idAlumno") Long idAlumno,
                                 Model model,
                                 RedirectAttributes redirectAttributes){
        this.cargarIdAlumnoYIdCurso(model, idAlumno, idCurso);

        return this.manejarBusinessExceptionConRedirect(()->{

            String nombreDeAlumno = alumnoService.obtenerNombreYApellidoDeAlumno(idAlumno);

            this.cargarNombreAlumno(model, nombreDeAlumno);
            model.addAttribute("notaForm", new NotaListDTO());
            return "docente/curso-nota-form";
        },
                "/docente/cursos/"+idCurso+"/alumnos/"+idAlumno+"/notas",
                redirectAttributes);
    }


    @PostMapping("/{idCurso}/alumnos/{idAlumno}/notas/nuevo")
    public String addNotaAlumnoDesdeForm(@PathVariable("idCurso") Long idCurso,
                                @PathVariable("idAlumno") Long idAlumno,
                                @RequestParam String nombreAlumno,
                                @Valid @ModelAttribute("notaForm") NotaListDTO notaForm,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes){

        this.cargarNombreAlumno(model, nombreAlumno);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "docente/curso-nota-form",
                redirectAttributes,
                "alumno.nota.success",
                "/docente/cursos/"+idCurso+"/alumnos/"+idAlumno+"/notas",
                ()->
                        notasService.cargarNotaA(idAlumno, idCurso, notaForm)
        );
    }



    @GetMapping("/{idCurso}/alumnos/{idAlumno}/notas/edit/{idNota}")
    public String editNotaAlumno(@PathVariable("idCurso") Long idCurso,
                                @PathVariable("idAlumno") Long idAlumno,
                                @PathVariable("idNota") Long idNota,
                                Model model,
                                RedirectAttributes redirectAttributes){

        this.cargarIdAlumnoYIdCurso(model, idAlumno, idCurso);
        this.indicarQueEsEditar(model);

        return this.manejarBusinessExceptionConRedirect(()->{
            String nombreDeAlumno = alumnoService.obtenerNombreYApellidoDeAlumno(idAlumno);
            NotaListDTO nota = notasService.obtenerNotaParaEditar(idNota);

            this.cargarNombreAlumno(model, nombreDeAlumno);
            model.addAttribute("notaForm", nota);

            return "docente/curso-nota-form";
        }, "/docente/cursos/"+idCurso+"/alumnos/"+idAlumno+"/notas",
                redirectAttributes);
    }



    @PostMapping("/{idCurso}/alumnos/{idAlumno}/notas/edit/{idNota}")
    public String editNotaAlumnoDesdeForm(@PathVariable("idCurso") Long idCurso,
                                          @PathVariable("idAlumno") Long idAlumno,
                                          @PathVariable("idNota") Long idNota,
                                          @RequestParam String nombreAlumno,
                                          @Valid @ModelAttribute("notaForm") NotaListDTO notaForm,
                                          BindingResult bindingResult,
                                          Model model,
                                          RedirectAttributes redirectAttributes){

        this.indicarQueEsEditar(model);
        this.cargarNombreAlumno(model, nombreAlumno);

        return this.manejarFormConRedirectEnExito(bindingResult,
                model,
                "docente/curso-nota-form",
                redirectAttributes,
                "nota.edit.success",
                "/docente/cursos/"+idCurso+"/alumnos/"+idAlumno+"/notas",
                ()->
                        notasService.actualizarNotaA(idAlumno, idCurso, notaForm, idNota)
        );
    }


    @PostMapping("/{idCurso}/alumnos/{idAlumno}/notas/{idNota}/eliminar")
    public String deleteNotaAlumno(@PathVariable("idCurso") Long idCurso,
                                   @PathVariable("idAlumno") Long idAlumno,
                                   @PathVariable("idNota") Long idNota,
                                   RedirectAttributes redirectAttributes) {

        return this.manejarBusinessExceptionConRedirect(() -> {
            this.notasService.removeNotaDeAlumnoDelCurso(idNota, idAlumno, idCurso);

            return redirectSuccess(redirectAttributes,
                    "nota.delete.success",
                    "/docente/cursos/" + idCurso + "/alumnos/" + idAlumno + "/notas");
        }, "/docente/cursos/" + idCurso + "/alumnos/" + idAlumno + "/notas",
                redirectAttributes);
    }


    //Utils

    public void cargarIdAlumnoYIdCurso(Model model, Long idAlumno, Long idCurso){
        this.cargarIdCurso(model, idCurso);
        model.addAttribute("idAlumno", idAlumno);
    }

    public void cargarIdCurso(Model model, Long idCurso){
        model.addAttribute("idCurso", idCurso);
    }

    public void cargarNombreAlumno(Model model, String nombre){
        model.addAttribute("nombreAlumno", nombre);
    }


}
