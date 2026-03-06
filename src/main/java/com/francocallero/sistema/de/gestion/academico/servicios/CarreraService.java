package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.carrera.CarreraDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Carrera;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoCarreras;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarreraService {

    private final RepoCarreras repoCarreras;


    @Cacheable
    public List<CarreraDTO> obtenerTodasLasCarrerasParaMostrar() {
        List<Carrera> carreras = repoCarreras.findAllByVirgenteTrue();

        return carreras.stream()
                .map(this::transformarACarreraDTO)
                .toList();
    }

    public void crearCarrera(CarreraDTO dto){
        if(repoCarreras.existsByNombreCarrera(dto.getNombreCarrera())){
            throw new BusinessException(ErrorCode.CARRERA_YA_EXISTE, dto.getNombreCarrera());
        }else{
            Carrera nuevaCarrera = new Carrera();
            nuevaCarrera.setNombreCarrera(dto.getNombreCarrera());

            repoCarreras.save(nuevaCarrera);
        }
    }

    private CarreraDTO transformarACarreraDTO(Carrera carrera){
        CarreraDTO dto = new CarreraDTO();

        dto.setIdCarrera(carrera.getIdCarrera());
        dto.setNombreCarrera(carrera.getNombreCarrera());

        return dto;
    }


    public Carrera obtenerCarreraConId(Long idCarrera) {
        return this.repoCarreras.findById(idCarrera)
                .orElseThrow(()->new BusinessException(ErrorCode.CARRERA_NO_EXISTE));
    }

    public CarreraDTO obtenerParaMostrar(Long idCarrera) {
        Carrera carrera = this.obtenerCarreraConId(idCarrera);

        return this.transformarACarreraDTO(carrera);
    }

    public void eliminarCarrera(Long idCarrera) {
        Carrera carrera = this.obtenerCarreraConId(idCarrera);
        carrera.setVirgente(false);

        repoCarreras.save(carrera);
    }

    public void editarCarrera(CarreraDTO form) {
        Carrera carrera = this.obtenerCarreraConId(form.getIdCarrera());

        carrera.setNombreCarrera(form.getNombreCarrera());
        repoCarreras.save(carrera);
    }
}
