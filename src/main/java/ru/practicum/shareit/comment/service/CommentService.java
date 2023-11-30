package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreatedCommentDto;

public interface CommentService {

    CreatedCommentDto createNewComment(CommentDto dto, Long itemId, Long authorId);
}
