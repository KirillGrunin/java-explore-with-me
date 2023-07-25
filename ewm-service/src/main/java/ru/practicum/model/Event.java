package ru.practicum.model;

import lombok.*;
import ru.practicum.util.LifeCycleState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @Column(name = "confirmed_Requests")
    private Integer confirmedRequests = 0;
    @Column(name = "created_On")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @Column(name = "eventDate")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    @Column(name = "lat")
    private Float lat;
    @Column(name = "lon")
    private Float lon;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_Limit")
    private Integer participantLimit;
    @Column(name = "published_On")
    private LocalDateTime publishedOn;
    @Column(name = "request_Moderation")
    private Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private LifeCycleState state;
    @Column(name = "title")
    private String title;
}