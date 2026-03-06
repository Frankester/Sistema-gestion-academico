package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepoInstancias extends JpaRepository<InstanciaExamen, Long> {

    @Query("Select i from InstanciaExamen i JOIN i.docentes d where d.documento = :documento")
    List<InstanciaExamen> findAllByDocenteDocumento(String documento);

    Optional<InstanciaExamen> findByMateriaAndFechaHora(Materia materia, LocalDateTime fechaHora);

    @Query("""
    SELECT e.alumno FROM InstanciaExamen ie
    JOIN ie.examenes e
    WHERE ie.idInstanciaExamen = :idInstanciaExamen
    AND (
        LOWER(e.alumno.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(e.alumno.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""")
    List<Alumno> findByInstanciaExamenAndNombreOrApellidoContainingIgnoreCase(
            Long idInstanciaExamen,
            @Param("search") String nombreOApellidoABuscar);

    @Query("""
            SELECT e.nota FROM InstanciaExamen ie
            JOIN ie.examenes e
            WHERE ie.idInstanciaExamen = :idInstanciaExamen
            AND e.alumno.idPersona = :idAlumno
            """)
    Optional<Integer> findNotaByInstanciaExamenAndAlumno(Long idInstanciaExamen, Long idAlumno);


    @Query("""
            SELECT e.alumno FROM InstanciaExamen ie
            JOIN ie.examenes e
            WHERE ie.idInstanciaExamen = :idInstanciaExamen
            """)
    List<Alumno> findAllAlumnosByInstanciaExamen(Long idInstanciaExamen);


    @Query("""
            SELECT COUNT(e) FROM Examen e
            WHERE e.instanciaExamen.idInstanciaExamen = :idInstanciaExamen
            """)
    Long countAlumnosByInstanciaExamen(Long idInstanciaExamen);


    @Query("""
            SELECT e FROM InstanciaExamen e
            WHERE LOWER(e.materia.nombre) LIKE LOWER(CONCAT('%', :nombreMateria, '%'))
            """)
    List<InstanciaExamen> findAllByNombreMateria(String nombreMateria)   ;
}
