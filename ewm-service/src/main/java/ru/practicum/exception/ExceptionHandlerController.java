package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler({NumberFormatException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError exceptionHandler(final Throwable e) {
        log.warn(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalStateException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiError exceptionHandlerBd(final Throwable e) {
        log.warn(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.CONFLICT);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiError exceptionHandler(final NotFoundException e) {
        log.warn(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.NOT_FOUND);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }
}