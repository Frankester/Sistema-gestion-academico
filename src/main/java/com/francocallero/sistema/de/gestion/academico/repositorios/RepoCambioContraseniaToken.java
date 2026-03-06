package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.contrasenias.CambioContraseniaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoCambioContraseniaToken extends JpaRepository<CambioContraseniaToken, Long> {
    Optional<CambioContraseniaToken> findByTokenHash(String token);
}
