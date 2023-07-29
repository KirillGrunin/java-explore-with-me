package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApiError extends RuntimeException {
    private List<String> errors = new ArrayList<>();
    private HttpStatus status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}