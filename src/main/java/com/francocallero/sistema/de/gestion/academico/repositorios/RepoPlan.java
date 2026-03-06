package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RepoPlan extends JpaRepository<Plan, Long> {
    @Query("""
           SELECT
             case
                when (count(p) > 0) then true
                else false
             end
           FROM Plan p
           WHERE p.carrera.idCarrera = :idCarrera
           AND p.nombre = :nombre""")
    boolean existsByNombreAndIdCarrera(String nombre, Long idCarrera);


    List<Plan> findAllByVirgenteTrue();
}
