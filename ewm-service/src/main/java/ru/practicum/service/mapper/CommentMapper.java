package ru.practicum.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(User user, Event event, NewCommentDto commentDto) {
        return Comment
                .builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .author(user)
                .event(event)
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .eventId(comment.getEvent().getId())
                .build();
    }
}