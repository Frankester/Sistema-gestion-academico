package com.francocallero.sistema.de.gestion.academico.controllers.DTOs.persona;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DatosPersonalesDTO {


    @NotBlank(message = "{documento.blank}")
    @Size(min= 8,max=8, message = "{documento.invalid-format}")
    private String documento;

    @NotBlank(message = "{nombre.blank}")
    private String nombre;

    @NotBlank(message = "{apellido.blank}")
    private String apellido;

    @Email(message = "{mail.invalid-format}")
    private String mail;

}
