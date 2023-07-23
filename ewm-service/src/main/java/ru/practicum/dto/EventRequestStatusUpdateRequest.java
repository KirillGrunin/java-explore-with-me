package ru.practicum.dto;

import lombok.*;
import ru.practicum.util.StatusRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private StatusRequest status;
}