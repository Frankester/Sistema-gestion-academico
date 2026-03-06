package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.contrasenia;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContraseniaNuevaDTO {

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[*+#.\"&§%°()|^@/¬`~<¡?=!¨\\]\\[])[A-Za-z\\d*+#.\"&§%°()|^@/¬`~<¡?=!¨\\]\\[]{8,}$",
            message = "{user.password.invalid-format}"
    )
    private String contraseniaNueva;
}
