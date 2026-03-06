package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.Examen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RepoExamenes extends JpaRepository<Examen, Long> {

    @Query("""
            SELECT e
            FROM Examen e
            WHERE e.alumno.idPersona = :idPersona
            AND e.instanciaExamen.materia.idMateria = :idMateria
            """)
    List<Examen> findAllExamenByIdAlumnoAndMateriaId(Long idPersona, Long idMateria);

    @Query("""
            SELECT e.instanciaExamen
            FROM Examen e
            WHERE e.alumno.idPersona = :idAlumno
            """)
    List<InstanciaExamen> findAllInscriptoByIdAlumno(Long idAlumno);
}
