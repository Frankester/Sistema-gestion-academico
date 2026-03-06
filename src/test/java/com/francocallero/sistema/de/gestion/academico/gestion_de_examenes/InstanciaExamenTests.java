package com.francocallero.sistema.de.gestion.academico.gestion_de_examenes;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstanciaExamenTests {

    private InstanciaExamen instanciaExamen;

    @Mock
    private Alumno alumno;
    @Mock
    private Materia materia;

    @BeforeEach
    void setup(){
        instanciaExamen = new InstanciaExamen();
        instanciaExamen.setMateria(materia);
        instanciaExamen.setFechaHora(LocalDateTime.now().plusDays(1));
        instanciaExamen.setSede("medrano");
    }

    @Test
    void sePuedeSaberSiDosInstanciasSonIdenticas_siFechaYMateriaCoinciden() {
        InstanciaExamen otra = new InstanciaExamen();
        otra.setFechaHora(instanciaExamen.getFechaHora());
        otra.setMateria(materia);
        otra.setSede(instanciaExamen.getSede());

        when(materia.equals(materia)).thenReturn(true);

        assertTrue(instanciaExamen.equals(otra));
    }

    @Test
    void sePuedeInscribirAlumno_siPuedeAnotarse() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);

        when(alumno.equals(alumno)).thenReturn(true);

        instanciaExamen.inscribirAAlumno(alumno);

        assertEquals(1, instanciaExamen.getExamenes().size());
        assertTrue(instanciaExamen.esAlumnoInscrito(alumno));
    }

    @Test
    void noSePuedeInscribirAlumno_siNoPuedeAnotarseYEstaVigente() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> instanciaExamen.inscribirAAlumno(alumno));
        assertEquals(ErrorCode.ALUMNO_EXAMEN_NO_PUEDE_INSCRIBIRSE,exception.getErrorCode());
    }

    @Test
    void sePuedeDetectarSiEsAlumnoInscrito() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);
        when(alumno.equals(alumno)).thenReturn(true);

        instanciaExamen.inscribirAAlumno(alumno);

        assertTrue(instanciaExamen.esAlumnoInscrito(alumno));
    }

    @Test
    void estaVigente_siFechaEsFutura() {
        instanciaExamen.setFechaHora(LocalDateTime.now().plusDays(1));
        assertTrue(instanciaExamen.estaVigente());
    }

    @Test
    void noEstaVigente_SiFechaEsPasada() {
        instanciaExamen.setFechaHora(LocalDateTime.now().minusDays(1));
        assertFalse(instanciaExamen.estaVigente());
    }

    @Test
    void puedeAnotarseAExamen_SiCumpleTodo() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);

        assertTrue(instanciaExamen.puedeAnotarse(alumno));
    }

    @Test
    void noPuedeAnotarse_SiYaEstaInscrito() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);
        when(alumno.equals(alumno)).thenReturn(true);

        instanciaExamen.inscribirAAlumno(alumno);

        assertFalse(instanciaExamen.puedeAnotarse(alumno));
    }

    @Test
    void sePuedeSaberLaNota_SiExiste() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);
        when(alumno.equals(alumno)).thenReturn(true);

        instanciaExamen.inscribirAAlumno(alumno);

        Docente docente = mock(Docente.class);
        instanciaExamen.agregarDocente(docente);

        instanciaExamen.subirNotaDe(alumno, 8, docente);

        int nota = instanciaExamen.getNotaDe(alumno);

        assertEquals(8, nota);
    }

    @Test
    void noSePuedeSaberLaNota_SiAlumnoNoEstaInscrito() {
       BusinessException exception = assertThrows(BusinessException.class,
                () -> instanciaExamen.getNotaDe(alumno));
        assertEquals(ErrorCode.ALUMNO_EXAMEN_DE_OTRO,exception.getErrorCode());
    }


    @Test
    void sePuedeSubirNotaCorrectamente() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);
        when(alumno.equals(alumno)).thenReturn(true);

        instanciaExamen.inscribirAAlumno(alumno);

        Docente docenteValido = new Docente();
        instanciaExamen.agregarDocente(docenteValido);

        Examen examen = new Examen();
        examen.setAlumno(alumno);
        examen.setNota(10);

        instanciaExamen.getExamenes().add(examen);
        instanciaExamen.subirNotaDe(alumno, 9, docenteValido);

        assertEquals(9, instanciaExamen.getNotaDe(alumno));
    }

    @Test
    void noSePuedeSubirNota_SiCondicionInvalidaEnSubirNota() {
        Docente docente = new Docente(); // no agregado

        BusinessException exception =assertThrows(BusinessException.class,
                () -> instanciaExamen.subirNotaDe(alumno, 10, docente));
        assertEquals(ErrorCode.DOCENTE_NO_PUEDE_SUBIR_NOTA_EXAMEN, exception.getErrorCode());
    }

    @Test
    void sePuedeSaberLaCantidadAlumnosInscritos() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);
        when(alumno.cursoMateria(materia)).thenReturn(true);
        when(alumno.terminoDeCursar(materia)).thenReturn(true);
        when(alumno.aproboMateria(materia)).thenReturn(true);

        instanciaExamen.inscribirAAlumno(alumno);

        assertEquals(1, instanciaExamen.getExamenes().size());
    }
}
