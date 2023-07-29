package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned = false;
    @Size(min = 1, max = 50)
    @NotBlank
    private String title;
}