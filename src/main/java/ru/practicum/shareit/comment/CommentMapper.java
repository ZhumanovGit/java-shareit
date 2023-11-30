package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreatedCommentDto;

@Component
public class CommentMapper {

    public Comment commentDtoToComment(CommentDto dto) {
        return Comment.builder()
                .text(dto.getText())
                .build();
    }

    public CreatedCommentDto commentToCreatedCommentDto (Comment comment) {
        return CreatedCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
