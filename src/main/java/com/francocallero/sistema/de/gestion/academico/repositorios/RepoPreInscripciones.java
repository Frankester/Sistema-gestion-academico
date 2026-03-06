package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Preinscripcion;
import org.hibernate.query.SelectionQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoPreInscripciones extends JpaRepository<Preinscripcion, Long> {


    @Query("SELECT p FROM Preinscripcion p WHERE p.estadoPreInscripcion = PENDIENTE ")
    List<Preinscripcion> findAllPendientes();

    @Query("""
            SELECT
                case
                    when(COUNT(p) > 0) THEN true
                    else false
                end
            FROM Preinscripcion p
            WHERE p.materia.idMateria = :idMateria
            AND p.comisionDeseada = :comision
            AND p.alumno.idPersona = :idPersona
            """)
    boolean existsPreinscripcionConComisionYMateria(String comision, Long idMateria, Long idPersona);

    @Query("""
            SELECT p
            FROM Preinscripcion p
            WHERE p.alumno.idPersona = :idAlumno
            """)
    List<Preinscripcion> findAllByIdAlumno(Long idAlumno);

    @Query("""
            SELECT p
            FROM Preinscripcion p
            WHERE p.alumno.idPersona = :idAlumno
            AND p.estadoPreInscripcion = PENDIENTE
            """)
    List<Preinscripcion> findAllPendienteByIdAlumno(Long idAlumno);

    @Query("""
            SELECT
                case
                    when(COUNT(p) > 0) THEN true
                    else false
                end
            FROM Preinscripcion p
            WHERE p.materia.idMateria = :idMateria
            AND p.estadoPreInscripcion = PENDIENTE
            """)
    boolean existsPreinscripcionPendienteConIdMateria(Long idMateria);
}
