package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import org.hibernate.query.SelectionQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepoCursos extends JpaRepository<Curso,Long> {

    List<Curso> findByVirgenteTrue();

    Optional<Curso> findByComisionIgnoreCase(String comision);

    @Query("""
    SELECT a FROM Alumno a
    JOIN a.cursadas c
    WHERE c.curso.idCurso = :idCurso
    AND (
        LOWER(a.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(a.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""")
    List<Alumno> findByCursoAndNombreOrApellidoContainingIgnoreCase(
            Long idCurso,
            @Param("search") String nombreOApellidoABuscar);


    @Query("""
            SELECT COUNT(ca) FROM Cursada ca
            WHERE ca.curso.idCurso = :idCurso
            """)
    Long countAlumnosByCurso(Long idCurso);


    @Query("""
            SELECT c FROM Curso c
            WHERE c.materia.idMateria = :idMateria
                AND c.virgente = true
            """)
    List<Curso> findAllByMateriaIdAndVirgenteTrue(Long idMateria);

    List<Curso> findAllByVirgenteTrue();

    @Query("""
            SELECT c FROM Curso c
            WHERE LOWER(c.materia.nombre) LIKE LOWER(CONCAT('%', :nombreMateria, '%'))
                AND c.virgente = true
            """)
    List<Curso> searchAllByVirgenteTrueAndMateriaNombre(String nombreMateria);
}
