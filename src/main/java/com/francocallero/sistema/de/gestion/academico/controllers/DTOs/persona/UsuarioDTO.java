package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.persona;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UsuarioDTO {

    @NotBlank(message = "{user.username.empty}")
    private String username;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[*+#.\"&§%°()|^@/¬`~<¡?=!¨\\]\\[])[A-Za-z\\d*+#.\"&§%°()|^@/¬`~<¡?=!¨\\]\\[]{8,}$",
            message = "{user.password.invalid-format}"
    )
    private String password;

    @NotBlank(message = "{user.authority.empty}")
    @Pattern(
            regexp = "admin|docente|alumno",
            message = "{user.authority.invalid-format}"
    )
    private String authority;

    @NotNull(message = "{user.plan.empty}")
    private Long idPlan;

    @NotBlank(message = "{user.document.empty}")
    private String documento;

    private String nombre;

    private String apellido;
}
