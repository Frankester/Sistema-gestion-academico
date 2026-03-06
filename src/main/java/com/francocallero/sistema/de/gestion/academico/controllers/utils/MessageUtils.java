package com.francocallero.sistema.de.gestion.academico.controllers.utils;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    private final MessageSource messageSource;

    @Autowired
    public MessageUtils(MessageSource messageSource){
        this.messageSource=messageSource;
    }

    public String getMessage(BusinessException exception){
        return messageSource.getMessage(
                exception.getErrorCode().getMessageKey(),
                exception.getArgs(),
                LocaleContextHolder.getLocale());
    }

    public String getMessage(String messageKey){
        return messageSource.getMessage(
                messageKey,
                null,
                LocaleContextHolder.getLocale());
    }

    public String getMessage(String messageKey, Object... args){
        return messageSource.getMessage(
                messageKey,
                args,
                LocaleContextHolder.getLocale());
    }

}
