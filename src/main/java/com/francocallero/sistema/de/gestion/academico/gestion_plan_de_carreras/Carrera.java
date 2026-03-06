package com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrera;

    @Column(unique = true,nullable = false)
    private String nombreCarrera;

    private boolean virgente;

    public Carrera() {
        this.virgente=true;
    }

    public boolean equals(Carrera carrera){
        return carrera.getNombreCarrera().equalsIgnoreCase(this.nombreCarrera);
    }
}
