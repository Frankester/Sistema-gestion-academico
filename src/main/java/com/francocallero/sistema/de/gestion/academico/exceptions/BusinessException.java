package com.francocallero.sistema.de.gestion.academico.exceptions;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException{

    private ErrorCode errorCode;

    private Object[] args;

    public BusinessException(ErrorCode errorCode, Object... args){
        this.errorCode=errorCode;
        this.args = args;
    }

    public BusinessException(ErrorCode errorCode){
        this.errorCode=errorCode;
        this.args = null;
    }
}
