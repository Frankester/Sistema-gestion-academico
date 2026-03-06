package com.francocallero.sistema.de.gestion.academico.controllers.docente;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.AbstractDatosPersonalesController;
import com.francocallero.sistema.de.gestion.academico.servicios.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/docente/datos")
public class DocenteDatosPersonalesController extends AbstractDatosPersonalesController {

    public DocenteDatosPersonalesController(PersonaService personaService) {
        super(personaService);
    }

    @Override
    protected String getViewForm() {
        return "docente/datos-personales-form";
    }

    @Override
    protected String getRedirectSuccessUrl() {
        return "/docente/datos";
    }


}
