package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.InstanciaExamenDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionExamenInstanciaService {

    private final UsuarioService usuarioService;
    private final InstanciaExamenService instanciaExamenService;
    private final ExamenService examenService;

    @Transactional
    public List<InstanciaExamenDTO> obtenerInstanciasExamenQuePuedeRendir(Usuario usuario) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);

        return instanciaExamenService.obtenerTodasLasInstanciasVirgentes().stream()
                .filter(instancia -> instancia.puedeAnotarse(alumno) &&
                        !examenService.aproboAlumnoExamenDeMateria(alumno.getIdPersona(),
                                instancia.getMateria()))
                .map(this::transformarAInstanciaExamenDTO)
                .toList();
    }

    @Transactional
    public void inscribirAlumnoAInstanciaExamen(Usuario usuario, Long idInstanciaExamen) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);

        InstanciaExamen instanciaExamen = instanciaExamenService
                .obtenerInstanciaExamen(idInstanciaExamen);

        instanciaExamen.inscribirAAlumno(alumno);
        instanciaExamenService.saveInstanciaExamen(instanciaExamen);
    }

    @Transactional
    public List<InstanciaExamenDTO> obtenerInstanciasExamenQuePuedeRendirConNombreMateria(
            Usuario usuario,
            String nombreMateria
    ) {
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);

       return instanciaExamenService.obtenerTodasLasInstanciasVirgentesConNombreMateria(nombreMateria)
               .stream()
               .filter(instancia -> instancia.puedeAnotarse(alumno) &&
                       !examenService.aproboAlumnoExamenDeMateria(
                               alumno.getIdPersona(),
                               instancia.getMateria()))
                .map(this::transformarAInstanciaExamenDTO)
                .toList();
    }

    @Transactional
    public List<InstanciaExamenDTO> obtenerInstanciasExamenInscriptoParaMostrar(Usuario usuario) {

        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);

        return examenService.obtenerInstanciasExamenesInscripto(alumno.getIdPersona()).stream()
                .map(instancia->{
                    InstanciaExamenDTO dto = this.transformarAInstanciaExamenDTO(instancia);
                    dto.setSePuedeEliminar(
                            this.sePuedeEliminarInscripcion(dto.getIdExamen(), alumno)
                    );

                    dto.setNota(instancia.getNotaDe(alumno));
                    return dto;
                })
                .toList();
    }

    @Transactional
    public void eliminarInscripcionAExamen(Usuario usuario, Long idInstanciaExamen) {
        InstanciaExamen instanciaExamen = instanciaExamenService
                .obtenerInstanciaExamen(idInstanciaExamen);
        Alumno alumno = (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);

        if(!this.sePuedeEliminarInscripcion(idInstanciaExamen, alumno) ||
                !instanciaExamen.esAlumnoInscrito(alumno)
        ){
            throw new BusinessException(ErrorCode.ALUMNO_EXAMEN_NO_PUEDE_ELIMINARSE);
        }else{
            instanciaExamen.eliminarInscripcionExamenDe(alumno);
            instanciaExamenService.saveInstanciaExamen(instanciaExamen);
        }
    }

    private  InstanciaExamenDTO transformarAInstanciaExamenDTO(InstanciaExamen instanciaExamen) {
        InstanciaExamenDTO dto = new InstanciaExamenDTO();

        dto.setIdExamen(instanciaExamen.getIdInstanciaExamen());
        dto.setSede(instanciaExamen.getSede());
        dto.setFecha(instanciaExamen.getFechaHora());
        dto.setMateriaNombre(instanciaExamen.getMateria().getNombre());
        return dto;
    }

    private boolean sePuedeEliminarInscripcion(Long idInstanciaExamen, Alumno alumno) {
        InstanciaExamen instanciaExamen = instanciaExamenService
                .obtenerInstanciaExamen(idInstanciaExamen);

        return instanciaExamen.estaVigente() && !instanciaExamen.tieneNota(alumno);
    }
}
