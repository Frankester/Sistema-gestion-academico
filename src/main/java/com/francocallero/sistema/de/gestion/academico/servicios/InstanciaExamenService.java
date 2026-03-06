package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.alumno.AlumnoNotaExamenListDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenFormDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenListadoAlumnosDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.examen.ExamenListadoDocentesDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoDocentes;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoInstancias;
import com.francocallero.sistema.de.gestion.academico.servicios.mappers.InstanciaExamenMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstanciaExamenService {

    private final RepoDocentes repoDocentes;
    private final RepoInstancias repoInstancias;

    private final MateriaService materiaService;
    private final UsuarioService usuarioService;

    private final InstanciaExamenMapper mapper;

    public void saveInstanciaExamen(InstanciaExamen instanciaExamen) {
        repoInstancias.save(instanciaExamen);
    }

    public InstanciaExamen crearExamenInstanciaExamen(Long materiaId,
                                                      LocalDateTime fecha,
                                                      List<Long> idDocentes,
                                                      String sede) {

        Materia materia= materiaService.findById(materiaId);

        Optional<InstanciaExamen> opFechaExamen = repoInstancias
                .findByMateriaAndFechaHora(materia, fecha);

        if(opFechaExamen.isPresent()){
            throw new BusinessException(ErrorCode.INSTANCIA_EXAMEN_EXISTENTE);
        }else {


            InstanciaExamen instanciaExamen = new InstanciaExamen();
            List<Docente> docentes = repoDocentes.findAllById(idDocentes);

            instanciaExamen.setDocentes(docentes);
            instanciaExamen.setFechaHora(fecha);
            instanciaExamen.setMateria(materia);
            instanciaExamen.setSede(sede);

            this.saveInstanciaExamen(instanciaExamen);
            return instanciaExamen;
        }
    }

    public InstanciaExamen obtenerInstanciaExamen(Long idInstancia) {
        return repoInstancias.findById(idInstancia)
                .orElseThrow(()->new BusinessException(ErrorCode.INSTANCIA_EXAMEN_NO_EXISTENTE));
    }
    @Transactional
    @Cacheable
    public List<ExamenListadoDocentesDTO> obtenerInstanciasParaMostrar() {
        return this.obtenerTodasLasInstanciasVirgentes().stream()
                .map(mapper::transformarAExamenListado)
                .toList();
    }

    @Transactional
    public ExamenFormDTO obtenerInstanciaParaEditar(Long id) {
        InstanciaExamen instanciaExamen = this.obtenerInstanciaExamen(id);
        return mapper.transformarAExamenForm(instanciaExamen);
    }

    @Transactional
    public void editarInstanciaExamen(ExamenFormDTO form, Long id)  {

        Long materiaId = form.getMateriaId();
        Materia materia = materiaService.findById(materiaId);

        InstanciaExamen instanciaExamen = this.obtenerInstanciaExamen(id);

        List<Docente> docentes =  repoDocentes.findAllById(
                form.getIdDocentes()
        );

        instanciaExamen.setIdInstanciaExamen(id);
        instanciaExamen.setDocentes(docentes);
        instanciaExamen.setMateria(materia);
        instanciaExamen.setFechaHora(form.getFecha());
        instanciaExamen.setSede(form.getSede());

        this.saveInstanciaExamen(instanciaExamen); //update instanciaExamen
    }

    @Transactional
    @Cacheable
    public List<ExamenListadoAlumnosDTO> getInstanciaExamenesDe(Usuario usuarioDocente) {
        Docente personaDocente =(Docente) usuarioService.obtenerPersonaDeUsuario(usuarioDocente);

        List<InstanciaExamen> instanciasDeexamenesDelDocente =repoInstancias
                .findAllByDocenteDocumento(personaDocente.getDocumento());

        return instanciasDeexamenesDelDocente.stream()
                .map(instancia->mapper.transformarAExamenListadoAlumnos(instancia,
                        repoInstancias.countAlumnosByInstanciaExamen(instancia.getIdInstanciaExamen())
                                .intValue()
                ))//fetch alumnosInscriptos from db
                .toList();
    }

    @Transactional
    public List<AlumnoNotaExamenListDTO> getAlumnosDeInstanciaExamen(Usuario usuarioDocente,
                                                                     Long idInstanciaExamen) {
        InstanciaExamen instanciaExamen = this.obtenerInstanciaExamen(idInstanciaExamen);

        this.verificarQueEsInstanciaDelDocente(instanciaExamen,usuarioDocente);

        return repoInstancias.findAllAlumnosByInstanciaExamen(idInstanciaExamen).stream()
                .map(alumno ->
                    //fetch fechaExamen and alumnosInscriptos form db
                     mapper.transformarAlumnoAAlumnoNotaList(
                            alumno,
                             this.obtenerNotaDeAlumnoenInstancia(idInstanciaExamen,alumno)
                     )
                )
                .toList();
    }

    @Transactional
    public List<AlumnoNotaExamenListDTO> buscarAlumnosDeInstanciaExamen(Usuario usuarioDocente,
                                                                        String nombreOApellidoAlumno,
                                                                        Long idInstanciaExamen){
        InstanciaExamen instanciaExamen = this.obtenerInstanciaExamen(idInstanciaExamen);

        this.verificarQueEsInstanciaDelDocente(instanciaExamen,usuarioDocente);

        return repoInstancias.findByInstanciaExamenAndNombreOrApellidoContainingIgnoreCase(
                idInstanciaExamen,
                        nombreOApellidoAlumno).stream()
                .map(alumno ->
                        //fetch fechaExamen and alumnosInscriptos form db
                        mapper.transformarAlumnoAAlumnoNotaList(
                                alumno,
                                this.obtenerNotaDeAlumnoenInstancia(idInstanciaExamen, alumno)
                        )
                )
                .toList();
    }

    public List<InstanciaExamen> obtenerTodasLasInstanciasVirgentes(){
        return repoInstancias.findAll().stream()
                .filter(InstanciaExamen::estaVigente)
                .toList();
    }

    public Integer obtenerNotaDeAlumnoenInstancia( Long idInstanciaExamen, Alumno alumno){
        return repoInstancias.findNotaByInstanciaExamenAndAlumno(
                        idInstanciaExamen, alumno.getIdPersona())
                .orElse(null);
    }


    public void validarSiExisteInstancia(Long idInstanciaExamen) {
        if(!this.repoInstancias.existsById(idInstanciaExamen)){
            throw new BusinessException(ErrorCode.INSTANCIA_EXAMEN_NO_EXISTENTE);
        }
    }


    public List<InstanciaExamen> obtenerTodasLasInstanciasVirgentesConNombreMateria(
            String nombreMateria
    ) {
        return this.repoInstancias.findAllByNombreMateria(nombreMateria).stream()
                .filter(InstanciaExamen::estaVigente)
                .toList();
    }

    private void verificarQueEsInstanciaDelDocente(InstanciaExamen instanciaExamen,
                                                   Usuario usuarioDocente){
        Docente docente =(Docente) usuarioService.obtenerPersonaDeUsuario(usuarioDocente);

        if(!instanciaExamen.esDocente(docente)){
            throw new BusinessException(ErrorCode.INSTANCIA_EXAMEN_DOCENTE_NO_PERTENECE);
        }
    }

    @Transactional
    public void eliminarInstanciaExamen(Long idInstanciaExamen) {
        InstanciaExamen instanciaExamen = this.obtenerInstanciaExamen(idInstanciaExamen);

        instanciaExamen.limpiarExamenesSiTiene();

        repoInstancias.deleteById(idInstanciaExamen);
    }
}