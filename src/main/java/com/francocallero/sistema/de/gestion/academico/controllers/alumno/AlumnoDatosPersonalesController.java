package com.francocallero.sistema.de.gestion.academico.controllers.alumno;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.AbstractDatosPersonalesController;
import com.francocallero.sistema.de.gestion.academico.servicios.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alumno/datos")
public class AlumnoDatosPersonalesController extends AbstractDatosPersonalesController {


    public AlumnoDatosPersonalesController(PersonaService personaService) {
        super(personaService);
    }

    @Override
    protected String getViewForm() {
        return "alumno/datos-personales-form";
    }

    @Override
    protected String getRedirectSuccessUrl() {
        return "/alumno/datos";
    }

}