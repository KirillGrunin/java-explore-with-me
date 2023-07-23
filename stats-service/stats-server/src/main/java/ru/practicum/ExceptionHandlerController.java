package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError exceptionHandler(final DateException e) {
        log.warn(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }
}