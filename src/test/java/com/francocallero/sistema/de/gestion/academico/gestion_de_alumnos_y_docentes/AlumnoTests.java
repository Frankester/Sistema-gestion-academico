package com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Curso;
import com.francocallero.sistema.de.gestion.academico.gestion_de_cursos.Cursada;
import com.francocallero.sistema.de.gestion.academico.gestion_de_examenes.InstanciaExamen;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import com.francocallero.sistema.de.gestion.academico.gestion_plan_de_carreras.Plan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlumnoTests {

    private Alumno alumno;

    @Mock private Plan plan;
    @Mock private Materia materia1;
    @Mock private Materia materia2;
    @Mock private Curso curso1;
    @Mock private Curso curso2;
    @Mock private Cursada cursada1;
    @Mock private Cursada cursada2;
    @Mock private InstanciaExamen instanciaExamen;

    @BeforeEach
    void setup(){
        alumno = new Alumno();
    }

    @Test
    void puedeListaDeMateriasQuePuedeInscribirse(){

        when(plan.getMaterias()).thenReturn(List.of(materia1, materia2));

        alumno.setPlanInscripto(plan);

        when(materia1.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(materia2.cumpleConCorrelativasParaCursar(alumno)).thenReturn(false);

        List<Materia> resultado = alumno.materiasQuePuedeInscribirse();

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(materia1));
        assertFalse(resultado.contains(materia2));
    }
    @Test
    void sePuedeObtenerLaNotaDeUnaMateria_siCursoLaMateria() {

        int notaFinalDelCurso = 6;

        when(cursada1.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada1.getCurso()).thenReturn(curso1);

        when(cursada2.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada2.getCurso()).thenReturn(curso2);

        when(curso1.getFechaInicioCurso()).thenReturn(LocalDate.of(2022, 1, 1));
        when(curso2.getFechaInicioCurso()).thenReturn(LocalDate.of(2023, 1, 1));

        when(curso2.getNotaFinalDeAlumno(alumno)).thenReturn(notaFinalDelCurso);

        alumno.getCursadas().addAll(List.of(cursada1, cursada2));

        int notaObtenida = alumno.getNotaDeMateria(materia1);

        assertEquals(notaFinalDelCurso, notaObtenida);
    }
    @Test
    void noSePuedeObtenerLaNotaDeUnaMateria_siNoCursoLaMateria() {

        when(materia1.getNombre()).thenReturn("Fisica 2");

        BusinessException exception = assertThrows(BusinessException.class,
                ()->alumno.getNotaDeMateria(materia1));

        assertEquals(ErrorCode.ALUMNO_NO_HIZO_LA_MATERIA,exception.getErrorCode());
    }
    @Test
    void sePuedeObtenerLaNotaDeExamen() {

        int notaDeExamen = 6;

        when(instanciaExamen.getNotaDe(alumno)).thenReturn(notaDeExamen);

        int notaObtenida = alumno.getNotaDeExamen(instanciaExamen);

        assertEquals(notaDeExamen, notaObtenida);
    }
    @Test
    void sePuedeSaberSiAproboUnaMateria(){

        int notaFinalDelCurso = 6;

        when(cursada1.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada1.getCurso()).thenReturn(curso1);
        when(curso1.getNotaFinalDeAlumno(alumno)).thenReturn(notaFinalDelCurso);


        alumno.getCursadas().add(cursada1);

        assertTrue(alumno.aproboMateria(materia1));
    }
    @Test
    void sePuedeSaberSiNoAproboUnaMateria(){

        int notaFinalDelCurso = 4;

        when(cursada1.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada1.getCurso()).thenReturn(curso1);
        when(curso1.getNotaFinalDeAlumno(alumno)).thenReturn(notaFinalDelCurso);

        alumno.getCursadas().add(cursada1);

        assertFalse(alumno.aproboMateria(materia1));
    }
    @Test
    void sePuedeSabersiCursoMateria() {
        when(cursada1.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada1.getCurso()).thenReturn(curso1);

        alumno.getCursadas().add(cursada1);

        assertTrue(alumno.cursoMateria(materia1));
    }
    @Test
    void sePuedeSabersiNoCursoMateria() {
        assertFalse(alumno.cursoMateria(materia1));
    }

    @Test
    void sePuedeSaberSiTerminoDeCursarAlumno(){
        when(cursada1.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada1.getCurso()).thenReturn(curso1);
        when(curso1.cerroCursadaDe(alumno)).thenReturn(true);

        alumno.getCursadas().add(cursada1);

        assertTrue(alumno.terminoDeCursar(materia1));
    }

    @Test
    void sePuedeSaberSiNoTerminoDeCursarAlumno(){
        when(cursada1.esDeLaMateria(materia1)).thenReturn(true);
        when(cursada1.getCurso()).thenReturn(curso1);
        when(curso1.cerroCursadaDe(alumno)).thenReturn(false);

        alumno.getCursadas().add(cursada1);

        assertFalse(alumno.terminoDeCursar(materia1));
    }
}
