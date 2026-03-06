package com.francocallero.sistema.de.gestion.academico.repositorios;

import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoCarreras extends JpaRepository<Carrera, Long> {

    boolean existsByNombreCarrera(String nombreCarrera);


    List<Carrera> findAllByVirgenteTrue();
}
