package com.francocallero.sistema.de.gestion.academico.persona;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Entity
public class Usuario implements UserDetails, CredentialsContainer {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioId;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="usuario_autorities", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(nullable = false)
    private Set<String> roles;

    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Getter
    private Long passwordVersion;

    @Getter
    private boolean perfilCompleto;


    public Usuario() {
        this.roles = new HashSet<>();
        this.passwordVersion = 0L;
        this.perfilCompleto = false;
    }

    public Usuario(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map((rol) -> new SimpleGrantedAuthority("ROLE_"+rol))
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void agregarRol(String role){
        this.roles.add(role);
    }

    @Override
    public void eraseCredentials() {
        this.setPassword(null);
    }

}
