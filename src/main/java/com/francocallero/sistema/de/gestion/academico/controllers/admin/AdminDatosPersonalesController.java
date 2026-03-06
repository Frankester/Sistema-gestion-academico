package com.francocallero.sistema.de.gestion.academico.controllers.admin;

import com.francocallero.sistema.de.gestion.academico.controllers.utils.AbstractDatosPersonalesController;
import com.francocallero.sistema.de.gestion.academico.servicios.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/datos")
public class AdminDatosPersonalesController extends AbstractDatosPersonalesController {

    protected AdminDatosPersonalesController(PersonaService personaService) {
        super(personaService);
    }

    @Override
    protected String getViewForm() {
        return "admin/datos-personales-form";
    }

    @Override
    protected String getRedirectSuccessUrl() {
        return "/admin/datos";
    }
}
