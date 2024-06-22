package com.example.Todo_list.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(NullEntityException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ModelAndView handleNullReferenceEntityException(NullEntityException exception) {
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, exception);
    }

    @ExceptionHandler(UserIsToDoOwnerException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ModelAndView handleUserIsToDoOwnerException(UserIsToDoOwnerException exception) {
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, exception);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleOtherExceptions(Exception exception) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private ModelAndView buildErrorResponse(HttpStatus status, Exception exception) {
        return new ModelAndView("error-page")
                    .addObject("code", status.value())
                    .addObject("codeDescription", status.getReasonPhrase())
                    .addObject("message", exception.getMessage());
    }
}
