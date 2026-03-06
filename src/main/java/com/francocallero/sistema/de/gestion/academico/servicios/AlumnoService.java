package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoAlumnos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final RepoAlumnos repoAlumnos;
    private final UsuarioService usuarioService;
    public Alumno obtenerAlumnoConDocumento(String documento) {
        return  repoAlumnos.findByDocumento(documento)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.ALUMNO_DOCUMENTO_NO_EXISTE, documento));
    }

    public void guardarAlumno(Alumno alumno) {
        repoAlumnos.save(alumno);
    }

    public Alumno obtenerAlumnoConId(Long idAlumno) {
        return repoAlumnos.findById(idAlumno)
                .orElseThrow(()->  new BusinessException(ErrorCode.ALUMNO_NO_EXISTE));
    }

    public String obtenerNombreYApellidoDeAlumno(Long idAlumno) {
        Alumno alumno = this.obtenerAlumnoConId(idAlumno);
        return alumno.getNombreYApellido();
    }

    public void verificarSiExisteAlumnoConId(Long idAlumno) {
        if(!repoAlumnos.existsById(idAlumno)){
            throw new BusinessException(ErrorCode.ALUMNO_NO_EXISTE);
        }
    }

    public Alumno obtenerAlumnoDeUsuario(Usuario usuario) {
        return (Alumno) usuarioService.obtenerPersonaDeUsuario(usuario);
    }
}
