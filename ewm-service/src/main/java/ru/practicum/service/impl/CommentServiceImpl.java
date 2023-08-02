package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;
import ru.practicum.service.mapper.CommentMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto saveComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с идентификатором : " + userId + " не найден"));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с идентификатором : " + userId + " не найдено"));
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(user, event, newCommentDto)));
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto commentDto) {
        checkUser(userId);
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден"));
        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        checkUser(userId);
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден")));
    }

    @Override
    public List<CommentDto> getCommentsUser(Long userId, PageRequest page) {
        checkUser(userId);
        return commentRepository.findAllByAuthorId(userId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsEvent(Long userId, Long eventId, PageRequest page) {
        checkUser(userId);
        if (!eventRepository.existsById(eventId))
            throw new NotFoundException("Событие с идентификатором : " + eventId + " не найдено");
        return commentRepository.findAllByEventId(eventId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        checkUser(userId);
        if (!commentRepository.existsById(commentId))
            throw new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден");
        commentRepository.deleteById(commentId);
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с идентификатором : " + userId + " не найден");
        }
    }

    private void checkComment(Long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден");
    }
}