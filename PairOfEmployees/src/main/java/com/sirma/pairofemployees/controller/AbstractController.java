package com.sirma.pairofemployees.controller;

import com.sirma.pairofemployees.model.error.BadRequestException;
import com.sirma.pairofemployees.model.error.ErrorDTO;
import com.sirma.pairofemployees.model.error.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

public abstract class AbstractController
{
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorDTO handlerNotFound(Exception e)
    {
        return buildErrorInfo(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorDTO handlerBadRequest(Exception e)
    {
        return buildErrorInfo(e, HttpStatus.BAD_REQUEST);
    }

    private ErrorDTO buildErrorInfo(Exception e, HttpStatus status)
    {
        e.printStackTrace();
        ErrorDTO dto = new ErrorDTO();
        dto.setStatus(status.value());
        dto.setMassage(e.getMessage());
        dto.setTime(LocalDateTime.now());
        return dto;
    }
}
