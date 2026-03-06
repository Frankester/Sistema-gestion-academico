package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.contrasenia;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class OlvidoContraseniaDTO {

    @Email(message = "{mail.invalid-format}")
    private String email;

}
