package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.persona.DatosPersonalesDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoPersona;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final RepoPersona repoPersona;
    private final UsuarioService usuarioService;

    public DatosPersonalesDTO obtenerDatosPersonalesParaMostrar(Usuario usuario) {

        Persona persona =usuarioService.obtenerPersonaDeUsuario(usuario);

        DatosPersonalesDTO dto = new DatosPersonalesDTO();

        dto.setApellido(persona.getApellido());
        dto.setNombre(persona.getNombre());
        dto.setMail(persona.getMail());
        dto.setDocumento(persona.getDocumento());
        return dto;
    }

    public void actualizarDatosPersonales(DatosPersonalesDTO form, Usuario usuario) {

        Persona persona =usuarioService.obtenerPersonaDeUsuario(usuario);

        Optional<Persona> opPersona = repoPersona.findByDocumento(form.getDocumento());
        if(opPersona.isPresent() && !opPersona.get().equals(persona)){
            throw new BusinessException(ErrorCode.PERSONA_YA_EXISTE_DOCUMENTO,form.getDocumento());
        }else {

            persona.setNombre(form.getNombre());
            persona.setApellido(form.getApellido());
            persona.setMail(form.getMail());
            persona.setDocumento(form.getDocumento());

            if(Objects.nonNull(form.getNombre()) && Objects.nonNull(form.getApellido()) &&
            Objects.nonNull(form.getMail()) && Objects.nonNull(form.getDocumento())){
                usuario.setPerfilCompleto(true);
                usuarioService.guardarUsuario(usuario);
            }

            repoPersona.save(persona);
        }
    }

}
