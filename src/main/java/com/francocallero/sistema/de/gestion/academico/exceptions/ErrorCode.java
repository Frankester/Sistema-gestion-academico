package com.francocallero.sistema.de.gestion.academico.exceptions;

public enum ErrorCode {
    USER_NOT_FOUND("user.not-found"),
    USER_USERNAME_ALREADY_EXIST("user.username.already-exist"),
    USER_DOCUMENTO_ALREADY_EXIST("user.documento.already-exist"),
    ALUMNO_NO_PUEDE_INSCRIBIRSE("alumno.no-puede-inscribirse"),
    ALUMNO_NO_HIZO_LA_MATERIA("alumno.no-hizo-la-materia"),
    ALUMNO_NO_TIENE_NOTA("alumno.no-tiene-nota"),
    ALUMNO_EXAMEN_DE_OTRO("alumno.examen-de-otro-alumno"),
    ALUMNO_EXAMEN_NO_TIENE_NOTA("alumno.examen-no-nota"),
    ALUMNO_EXAMEN_NO_EXISTE("alumno.examen-no-existe"),
    ALUMNO_NO_TIENE_NOTAS("alumno.no-tiene-notas"),
    ALUMNO_DOCUMENTO_NO_EXISTE("alumno.documento.no-existe"),
    ALUMNO_NO_PERTENECE_CURSO("alumno.curso.no-pertenece"),
    ALUMNO_NO_EXISTE("alumno.no-existe"),
    ALUMNO_YA_ESTA_MATRICULADO("alumno.ya-matriculado"),
    ALUMNO_EXAMEN_NO_PUEDE_INSCRIBIRSE("alumno.examen.no-puede-inscribirse"),
    ALUMNO_EXAMEN_NO_PUEDE_ELIMINARSE("alumno.examen.no-puede-eliminarse"),
    ALUMNO_MATERIA_YA_LA_CURSO("alumno.materia.ya-la-curso"),
    ALUMNO_MATERIA_YA_LA_APROBO("alumno.materia.ya-la-aprobo"),
    PERSONA_NO_EXISTE("persona.no-existe"),
    PERSONA_YA_EXISTE_DOCUMENTO("persona.existe-documento"),
    DOCENTE_NO_EXISTE("docente.no-existe"),
    DOCENTE_NO_PUEDE_SUBIR_NOTA_EXAMEN("docente.examen.nota.no-puede-subir"),
    DOCENTE_NO_PUEDE_CERRAR_CURSADA("docente.cursada.no-puede-cerrar"),
    DOCENTE_CURSADA_NO_PUEDE_CAMBIAR_NOTA("docente.cursada.no-puede-editar-nota"),

    DOCENTE_CURSO_NO_PUEDE_CAMBIAR_NOTA("docente.curso.no-puede-editar-nota"),

    DOCENTE_CURSO_NO_PERTENECE("docente.curso.no-pertenece"),
    CURSO_EXISTENTE("curso.existente"),
    CURSO_NOT_FOUND("curso.not-found"),
    CURSO_NO_VIRGENTE("curso.no-virgente"),
    INSTANCIA_EXAMEN_EXISTENTE("instancia-examen.existente"),
    INSTANCIA_EXAMEN_NO_EXISTENTE("instancia-examen.no-existente"),
    INSTANCIA_EXAMEN_DOCENTE_NO_PERTENECE("instancia-examen.docente.no-pertenece"),
    INSTANCIA_EXAMEN_EXAMEN_NO_EXISTE("instancia-examen.examen.no-existe"),
    ADMIN_NO_EXISTE_MATERIA("admin.materia.no-existe"),
    ADMIN_DOCENTE_NO_EXISTE("admin.docente.no-existe"),
    NOTA_NO_EXISTE("nota.no-existe"),
    HORARIO_CURSADA_NO_EXISTE("horario.no-existe"),
    HORARIO_CURSADA_INVALIDO("horario.invalido"),
    MATERIA_NO_EXISTE("materia.no-existe"),
    MATERIA_CORRELATIVA_YA_EXISTE("materia.correlativa.existe"),
    EXAMEN_NO_EXISTE("examen.no-existe"),
    PRE_INSCRIPCION_NO_EXISTE("preinscripcion.no-existe"),
    PRE_INSCRIPCION_DE_OTRO_ALUMNO("preinscripcion.de-otro-alumno"),
    PRE_INSCRIPCION_ELIMINAR_NO_PERMITIDO("preinscripcion.eliminar.no-permitido"),
    PRE_INSCRIPCION_MATERIA_REPETIDA("preinscripcion.materia-repetida"),
    PLAN_NO_EXISTE("plan.no-existe"),
    PLAN_MATERIA_NO_PERTENECE("plan.materia.no-pertenece"),
    PLAN_MATERIA_REPETIDA("plan.materia.ya-existe"),
    PLAN_YA_EXISTE("plan.existe"),
    CARRERA_YA_EXISTE("carrera.existe"),
    CARRERA_NO_EXISTE("carrera.no-existe"),
    CONFLICTO_HORARIOS("horarios.conflicto"),
    CAMBIO_CONTRASENIA_NO_EXISTE("cambio.contrasenia.no-existe"),
    TOKEN_INVALIDO("token.invalido");


    private final String messageKey;

    ErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

}
