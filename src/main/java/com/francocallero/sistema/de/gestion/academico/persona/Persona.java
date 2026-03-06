package com.francocallero.sistema.de.gestion.academico.persona;

import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="PERSONA")
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @Column(unique = true)
    private String documento;

    private String nombre;

    private String apellido;

    private String mail;

    @Enumerated(value=EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    public boolean equals(Persona persona){
        return this.getDocumento().equals(persona.getDocumento());
    }

    public String getNombreYApellido(){
        return this.nombre + " " + this.apellido;
    }
}
