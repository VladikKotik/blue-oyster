package com.blue.oyster.controller;

import com.blue.oyster.model.exceptions.ClearingHistoryException;
import com.blue.oyster.model.exceptions.GuestNotFoundException;
import com.blue.oyster.model.exceptions.InvalidQrCodeException;
import com.blue.oyster.model.exceptions.NoFiltrationSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(GuestNotFoundException.class)
    public ProblemDetail handleNotFound(GuestNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }


    @ExceptionHandler(InvalidQrCodeException.class)
    public ProblemDetail handleNotFound(InvalidQrCodeException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.EXPECTATION_FAILED,
                ex.getMessage()
        );
    }


    @ExceptionHandler(ClearingHistoryException.class)
    public ProblemDetail handleNotFound(ClearingHistoryException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(NoFiltrationSet.class)
    public ProblemDetail handleNoFiltrationSet(NoFiltrationSet ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }
}
