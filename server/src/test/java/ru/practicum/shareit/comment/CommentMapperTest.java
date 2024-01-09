package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    CommentMapper mapper = new CommentMapper();

    @Test
    public void commentCreateDtoToCommentTest() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("text")
                .build();

        Comment result = mapper.commentCreateDtoToComment(dto);

        assertEquals(dto.getText(), result.getText());
    }

    @Test
    public void commentToCommentDtoTest() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .name("name")
                .build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .item(item)
                .author(user)
                .created(now)
                .build();

        CommentDto result = mapper.commentToCommentDto(comment);

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreated(), result.getCreated());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
    }
}