package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository(value = "usuarios")
public interface RepoUsuarios extends JpaRepository<Usuario,Long> {

    Optional<Usuario> findByUsername(String username);

    @Query("SELECT r FROM Usuario u JOIN u.roles r where u.username = :username")
    Set<String> findRolesByUsername(@Param("username") String username);


    @Query("SELECT u FROM Usuario u JOIN u.persona r where r.documento = :documento")
    Optional<Usuario> findByDocumento(String documento);

    @Query("SELECT p FROM Usuario u JOIN u.persona p where u.usuarioId = :usuarioId")
    Optional<Persona> findPersonaByUsuarioId(Long usuarioId);

    @Query("""
            SELECT u from Usuario u
            WHERE u.persona.mail = :mail
            """)
    Optional<Usuario> findByPersonaMail(String mail);

    @Query("""
            SELECT u.passwordVersion
            FROM Usuario u
            WHERE u.usuarioId = :usuarioId
            """)
    Long findPasswordVersionById(Long usuarioId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Usuario u SET u.username = :#{#usuario.username},
            u.passwordVersion = :#{#usuario.passwordVersion},
            u.perfilCompleto = :#{#usuario.perfilCompleto},
            u.persona = :#{#usuario.persona}
            WHERE u.usuarioId = :#{#usuario.usuarioId}
            """)
    void updateUsuario(Usuario usuario);
}
