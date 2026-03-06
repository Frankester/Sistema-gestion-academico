package com.francocallero.sistema.de.gestion.academico.gestion_de_cursos;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CursadaTests {

    private Cursada cursada;


    @BeforeEach
    void setup(){
        this.cursada = new Cursada();
    }


    @Test
    void sePuedeCalcularLaNotaFinal(){
        Nota nota1= new Nota();
        Nota nota2 = new Nota();
        Nota nota3 = new Nota();
        nota1.setNota(6);
        nota2.setNota(5);
        nota3.setNota(5);
        cursada.setNotas(List.of(nota1, nota2, nota3));

        cursada.cerrarCursada();
        cursada.calcularNotaFinal();

        int notaFinal = cursada.getNotaFinal();

        Assertions.assertEquals(6,notaFinal);//redondeo para arriba
    }

    @Test
    void noSePuedeCalcularLaNotaFinal_siNoTieneNotas() {
        cursada.setNotas(List.of());

        BusinessException exception = assertThrows(BusinessException.class,
                ()-> cursada.calcularNotaFinal());

        assertEquals(ErrorCode.ALUMNO_NO_TIENE_NOTAS, exception.getErrorCode());
    }
}
