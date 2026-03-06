package com.francocallero.sistema.de.gestion.academico.controllers.utils;

import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ControllerBase {

    @Autowired
    private MessageUtils messageUtils;

    private final Logger logger = LoggerFactory.getLogger(ControllerBase.class);

    private final String ERROR_LOGGER_MESSAGE_FORMAT =  "Oh no a ocurrido un error!! : {}";

    protected String manejarBusinessExceptionSinRedirect(Supplier<String> action,
                                                         String viewARenderizarEnError,
                                                         Model model,
                                                         @Null BindingResult bindingResult){

        if(bindingResult != null && bindingResult.hasErrors()){
            return viewARenderizarEnError;
        }

        try{

            return action.get();
        }catch (BusinessException exception){
            String errorMessage = this.messageUtils.getMessage(exception);
            model.addAttribute("error", errorMessage);
            logger.error(ERROR_LOGGER_MESSAGE_FORMAT, errorMessage);
            return viewARenderizarEnError;
        }
    }


    protected String manejarBusinessExceptionConRedirect(Supplier<String> action,
                                                         String redirectUrlError,
                                                         RedirectAttributes redirectAttributes) {
        try{
            return action.get();
        }catch (BusinessException exception){
            String errorMessage = messageUtils.getMessage(exception);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            logger.error(ERROR_LOGGER_MESSAGE_FORMAT, errorMessage);
            return "redirect:"+redirectUrlError;
        }

    }


    protected String redirectSuccess(RedirectAttributes redirectAttributes,
                                  String messgaeKey,
                                  String urlToRedirect){
        String message = this.messageUtils.getMessage(messgaeKey);
        redirectAttributes.addFlashAttribute("success", message);

        return "redirect:"+urlToRedirect;
    }


    protected String manejarFormConRedirectEnExito(
            BindingResult bindingResult,
            Model model,
            String viewError,
            RedirectAttributes redirectAttributes,
            String messageKeySuccess,
            String redirectUrlSuccess,
            Runnable action
    ) {
        return this.manejarBusinessExceptionSinRedirect(() -> {
            action.run();
            return this.redirectSuccess(
                    redirectAttributes,
                    messageKeySuccess,
                    redirectUrlSuccess);
        }, viewError, model, bindingResult);
    }

    protected <T> List<T> manejarBusqueda(
            String search,
            Supplier<List<T>> findAll,
            Function<String, List<T>> searchFunction) {

        if (search != null && !search.isBlank()) {
            return searchFunction.apply(search);
        }
        return findAll.get();
    }

    protected void indicarQueEsEditar(Model model){
        model.addAttribute("esEditar", true);
    }
}
