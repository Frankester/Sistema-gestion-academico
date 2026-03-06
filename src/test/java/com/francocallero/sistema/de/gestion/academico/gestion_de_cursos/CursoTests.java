package com.francocallero.sistema.de.gestion.academico.gestion_de_cursos;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_materias.Materia;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CursoTests {

    private Curso curso;

    @Mock
    private Materia materia;
    @Mock
    private Alumno alumno;
    @Mock
    private Cursada cursada;
    @Mock
    private Nota nota;


    @BeforeEach
    void setup(){
        curso = new Curso();
        curso.setMateria(materia);
        curso.setComision("A1");
        curso.setFechaInicioCurso(LocalDate.now());
    }

    @Test
    void dosCursosSonIdenticos_SiMateriaYComisionCoinciden() {
        Curso otro = new Curso();
        otro.setMateria(materia);
        otro.setComision("a1"); // case insensitive

        when(materia.equals(materia)).thenReturn(true);

        assertTrue(curso.equals(otro));
    }

    @Test
    void sePuedeObtenerLasNotasDelAlumnoEnElCurso() {
        when(cursada.getAlumno()).thenReturn(alumno);
        when(cursada.getNotas()).thenReturn(List.of(nota));
        when(alumno.equals(alumno)).thenReturn(true);

        curso.getCursadas().add(cursada);

        List<Nota> notas = curso.getNotasDelAlumno(alumno);

        assertEquals(1, notas.size());
    }

    @Test
    void noSePuedeObtenerLasNotasDelAlumno_SiNoPerteneceAlCurso() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> curso.getNotasDelAlumno(alumno));
        assertEquals(ErrorCode.ALUMNO_NO_PERTENECE_CURSO, exception.getErrorCode());
    }

    @Test
    void sePuedeAgregarNotaAlAlumno() {
        when(cursada.getAlumno()).thenReturn(alumno);
        when(alumno.equals(alumno)).thenReturn(true);

        curso.getCursadas().add(cursada);

        curso.agregarNotaA(alumno, nota);

        verify(cursada).agregarNota(nota);
    }

    @Test
    void sePuedeEliminarNotaDelAlumno() {
        when(cursada.getAlumno()).thenReturn(alumno);
        when(alumno.equals(alumno)).thenReturn(true);

        curso.getCursadas().add(cursada);

        curso.eliminarNotaDe(alumno, nota);

        verify(cursada).quitarNota(nota);
    }

    @Test
    void sePuedeInscribirAlumno_SiCumpleCondiciones() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(true);

        curso.inscribirAlumno(alumno);

        assertEquals(1, curso.getCursadas().size());
    }

    @Test
    void noSePuedeInscribirAlumno_SiYaEsAlumno() {
        when(cursada.getAlumno()).thenReturn(alumno);
        when(cursada.seDioDeBaja()).thenReturn(false);
        when(alumno.equals(alumno)).thenReturn(true);

        curso.getCursadas().add(cursada);

        BusinessException exception =  assertThrows(BusinessException.class,
                () -> curso.inscribirAlumno(alumno));

        assertEquals(ErrorCode.ALUMNO_YA_ESTA_MATRICULADO, exception.getErrorCode());
    }

    @Test
    void noSePuedeInscribirAlumno_SiNoCumpleCorrelativas() {
        when(materia.cumpleConCorrelativasParaCursar(alumno)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> curso.inscribirAlumno(alumno));
        assertEquals(ErrorCode.ALUMNO_NO_PUEDE_INSCRIBIRSE, exception.getErrorCode());
    }


    @Test
    void sePuedeObtenerLaNotaFinal() {
        when(cursada.getAlumno()).thenReturn(alumno);
        when(cursada.getNotaFinal()).thenReturn(8);
        when(alumno.equals(alumno)).thenReturn(true);

        curso.getCursadas().add(cursada);

        Integer notaFinal = curso.getNotaFinalDeAlumno(alumno);

        assertEquals(8, notaFinal);
    }

    @Test
    void sePuedeDarDeBajaAlAlumno() {
        when(cursada.getAlumno()).thenReturn(alumno);
        when(cursada.seDioDeBaja()).thenReturn(false);
        when(alumno.equals(alumno)).thenReturn(true);

        curso.getCursadas().add(cursada);

        curso.darDeBajaA(alumno);

        verify(cursada).abandonarCursada();
    }

    @Test
    void noSePuedeDarDeBajaAlAlumno_SiAlumnoNoPerteneceAlCurso() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> curso.darDeBajaA(alumno));

        assertEquals(ErrorCode.ALUMNO_NO_PERTENECE_CURSO, exception.getErrorCode());
    }

    @Test
    void sePuedeObtenerListaDeAlumnosInscriptos() {
        when(cursada.getAlumno()).thenReturn(alumno);

        curso.getCursadas().add(cursada);

        List<Alumno> alumnos = curso.getAlumnosInscriptos();

        assertEquals(1, alumnos.size());
        assertTrue(alumnos.contains(alumno));
    }
}
