package com.francocallero.sistema.de.gestion.academico.gestion_de_materias;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MateriaTests {

    private Materia materia;

    @Mock
    private Materia correlativa1;
    @Mock
    private Materia correlativa2;
    @Mock
    private Materia correlativa3;
    @Mock
    private Alumno alumno;


    @BeforeEach
    void setup(){
        materia = new Materia();
        materia.setNombre("Fisica 2");
        materia.setAnioPerteneciente(2);

        List<Materia> correlativas = new ArrayList<>(List.of(correlativa1, correlativa2, correlativa3));
        materia.setCorrelativas(correlativas);
    }

    @Test
    void puedeCursar_siCumpleConLasCorrelativasParaCursar(){

        when(alumno.cursoMateria(any(Materia.class)))
                .thenReturn(true);

        assertTrue(materia.cumpleConCorrelativasParaCursar(alumno));
    }

    @Test
    void noPuedeCursar_siNoCumpleConAlgunaCorrelativaParaCursar(){

        when(alumno.cursoMateria(correlativa1)).thenReturn(true);

        when(alumno.cursoMateria(correlativa2)).thenReturn(false);

        assertFalse(materia.cumpleConCorrelativasParaCursar(alumno));
    }

    @Test
    void noPuedeCursar_siNoCursoNingulaMateria(){

        when(alumno.cursoMateria(any(Materia.class)))
                .thenReturn(false);

        assertFalse( materia.cumpleConCorrelativasParaCursar(alumno));
    }


    @Test
    void puedeAprobar_siCumpleConLasCorrelativasParaAprobar()  {

        when(alumno.aproboMateria(any(Materia.class))).thenReturn(true);
        when(alumno.terminoDeCursar(any(Materia.class))).thenReturn(true);
        when(alumno.cursoMateria(any(Materia.class))).thenReturn(true);

        boolean resultado = materia.cumpleConCorrelativasParaAprobar(alumno);

        assertTrue(resultado);
    }

    @Test
    void noPuedeAprobar_siNoAproboAlgunaCorrelativa()  {

        when(alumno.terminoDeCursar(any(Materia.class))).thenReturn(true);
        when(alumno.cursoMateria(any(Materia.class))).thenReturn(true);

        when(alumno.aproboMateria(correlativa1)).thenReturn(true);
        when(alumno.aproboMateria(correlativa2)).thenReturn(false);

        assertFalse(materia.cumpleConCorrelativasParaAprobar(alumno));
    }

    @Test
    void sePuedeAgregarUnaCorrelativa_SiNoFueAgregadaAntes(){
        Materia correlativa4 = mock(Materia.class);

        materia.agregarCorrelativa(correlativa4);

        assertEquals( List.of(correlativa1, correlativa2, correlativa3, correlativa4) ,
                materia.getCorrelativas());
    }

    @Test
    void noSePuedeAgregarUnaCorrelativa_SiYaFueAgregadaAntes(){

        BusinessException exception = assertThrows(BusinessException.class,
            ()->materia.agregarCorrelativa(correlativa3));

        assertEquals(ErrorCode.MATERIA_CORRELATIVA_YA_EXISTE,
                exception.getErrorCode());
    }

}
