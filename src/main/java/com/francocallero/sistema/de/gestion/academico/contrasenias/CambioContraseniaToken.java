package com.francocallero.sistema.de.gestion.academico.contrasenias;

import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CambioContraseniaToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCambioContraseniaToken;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(unique = true, nullable = false)
    private String tokenHash;

    private boolean usado;
    private LocalDateTime fechaHoraExpiracion;

    public boolean vencioContrasenia() {
        return LocalDateTime.now().isAfter(this.fechaHoraExpiracion);
    }
}
