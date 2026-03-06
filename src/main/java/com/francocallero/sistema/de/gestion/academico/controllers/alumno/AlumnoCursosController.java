package com.francocallero.sistema.de.gestion.academico.controllers.alumno;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.curso.CursoAlumnoDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.utils.ControllerBase;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.servicios.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/alumno/cursos")
@RequiredArgsConstructor
public class AlumnoCursosController extends ControllerBase {

    private final String URL_CURSOS= "/alumno/cursos";

    private final CursoService cursoService;

    @GetMapping
    public String mostrarCursos(@RequestParam(required = false) String search,
                                Model model,
                                @AuthenticationPrincipal Usuario usuario){


        List<CursoAlumnoDTO> cursos = this.manejarBusqueda(search,
                ()->cursoService.obtenerTodosParaMostrarAAlumno(usuario),
                (nombreMateria)-> cursoService.obtenerTodosparaMostrarConNombre(nombreMateria, usuario)
                );
        model.addAttribute("cursos", cursos);

        return "alumno/cursos-actuales";
    }

    @GetMapping("/{idCurso}")
    public String mostrarNotasDeUnCurso(@PathVariable Long idCurso,
                                        @AuthenticationPrincipal Usuario usuario,
                                        Model model,
                                        RedirectAttributes redirectAttributes){


        return this.manejarBusinessExceptionConRedirect(()->{

            model.addAttribute("curso",
                    cursoService.obtenerCursoConIdParaMostrar(idCurso, usuario));

            return "alumno/curso-detalle";
        },URL_CURSOS,
                redirectAttributes);
    }

}
