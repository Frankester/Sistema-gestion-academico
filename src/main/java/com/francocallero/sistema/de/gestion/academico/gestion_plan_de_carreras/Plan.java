package com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlan;

    @OneToMany(mappedBy = "plan")
    private List<Materia> materias;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @OneToMany(mappedBy = "planInscripto", cascade = CascadeType.ALL)
    private List<Alumno> alumnosConElPlan;

    private boolean virgente;

    public Plan(){
        this.materias = new ArrayList<>();
        this.alumnosConElPlan= new ArrayList<>();
        this.virgente= true;
    }


    public boolean equals(Plan plan){
        return plan.getNombre().equalsIgnoreCase(this.getNombre()) &&
                plan.getCarrera().equals(this.getCarrera());
    }

    public void sacarMateria(Materia materia) {
        if(!this.perteneceAlPlan(materia)){
           throw new BusinessException(ErrorCode.PLAN_MATERIA_NO_PERTENECE);
        }

        materia.setPlan(null);
        this.materias.remove(materia);
    }

    public boolean existeMateriaConNombre(String nombre) {
        return this.materias.stream()
                .anyMatch(materia -> materia.getNombre().equalsIgnoreCase(nombre));
    }


    private boolean perteneceAlPlan(Materia materia) {
        return this.materias.contains(materia);
    }
}
