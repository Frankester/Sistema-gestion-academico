package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RepoMaterias extends JpaRepository<Materia, Long> {

    @Modifying
    @Query(value = "DELETE FROM materia_correlativa WHERE id_correlativa = :idMateria", nativeQuery = true)
    void eliminarReferenciasComoCorrelativa(Long idMateria);
}
