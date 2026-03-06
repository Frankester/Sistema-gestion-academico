package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.NotaExamenFormDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final RepoDocentes repoDocentes;
    private final InstanciaExamenService instanciaExamenService;
    private final UsuarioService usuarioService;
    private final AlumnoService alumnoService;


    @Cacheable
    public List<Docente> findAll() {
        return repoDocentes.findAll();
    }

    public Docente findByDocumento(String number) {
       return repoDocentes.findByDocumento(number)
               .orElseThrow(()-> new BusinessException(ErrorCode.ADMIN_DOCENTE_NO_EXISTE,number));
    }

    public Docente findById(Long idDocente) {
        return repoDocentes.findById(idDocente)
                .orElseThrow(()->new BusinessException(ErrorCode.ADMIN_DOCENTE_NO_EXISTE,idDocente));
    }


    @Transactional
    public void  subirNotaExamen(Usuario usuarioDocente, NotaExamenFormDTO form) {

        Docente docente =(Docente) usuarioService.obtenerPersonaDeUsuario(usuarioDocente);

        InstanciaExamen instanciaExamen =instanciaExamenService.obtenerInstanciaExamen(form.getIdInstanciaExamen());

        Alumno alumno = alumnoService.obtenerAlumnoConId(form.getIdAlumno());

        instanciaExamen.subirNotaDe(alumno, form.getNota(), docente);
    }

}
