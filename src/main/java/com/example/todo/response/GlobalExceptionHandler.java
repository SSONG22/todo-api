package com.example.todo.response;

import com.example.todo.response.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        log.warn(exception.getMessage());
        ErrorCode errorCode = exception.getErrorCode();
        return new ResponseEntity(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.warn(exception.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return new ResponseEntity(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({NoHandlerFoundException.class, MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorResponse> handleNotFoundException(NoHandlerFoundException exception) {
        log.warn(exception.getMessage());
        ErrorCode errorCode = ErrorCode.NOT_FOUND;
        return new ResponseEntity(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    protected ResponseEntity<ErrorResponse> handleBadRequestException(Exception exception) {
        log.warn(exception.getMessage());
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return new ResponseEntity(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({HttpClientErrorException.Unauthorized.class})
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(HttpClientErrorException.Unauthorized exception) {
        log.warn(exception.getMessage());
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return new ResponseEntity(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getStatus()));
    }

}
