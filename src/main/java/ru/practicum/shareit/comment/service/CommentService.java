package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;

public interface CommentService {

    CommentDto createNewComment(CommentCreateDto dto, Long itemId, Long authorId);
}
