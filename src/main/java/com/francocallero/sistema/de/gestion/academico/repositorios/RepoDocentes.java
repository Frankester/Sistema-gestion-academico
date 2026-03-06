package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoDocentes extends JpaRepository<Docente, Long> {
    Optional<Docente> findByDocumento(String number);
}
