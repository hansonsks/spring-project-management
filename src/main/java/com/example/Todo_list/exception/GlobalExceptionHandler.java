package com.example.Todo_list.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles EntityNotFoundException
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    /**
     * Handles NullEntityException
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(NullEntityException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ModelAndView handleNullReferenceEntityException(NullEntityException exception) {
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, exception);
    }

    /**
     * Handles UserIsToDoOwnerException
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(UserIsToDoOwnerException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ModelAndView handleUserIsToDoOwnerException(UserIsToDoOwnerException exception) {
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, exception);
    }

    /**
     * Handles NoHandlerFoundException
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    /**
     * Handles AccessDeniedException
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDeniedException(AccessDeniedException exception) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception);
    }

    /**
     * Handles NoResourceFoundException
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    /**
     * Handles Exception
     * @param exception
     * @return ModelAndView
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleOtherExceptions(Exception exception) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    /**
     * Builds error response
     * @param status
     * @param exception
     * @return ModelAndView
     */
    private ModelAndView buildErrorResponse(HttpStatus status, Exception exception) {
        return new ModelAndView("error-page")
                    .addObject("code", status.value())
                    .addObject("codeDescription", status.getReasonPhrase())
                    .addObject("message", exception.getMessage());
    }
}
