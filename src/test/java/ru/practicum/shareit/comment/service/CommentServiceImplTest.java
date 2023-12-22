package ru.practicum.shareit.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.model.PostCommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceImplTest {
    CommentRepository commentRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    CommentMapper mapper;
    CommentServiceImpl service;

    @BeforeEach
    public void beforeEach() {
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        mapper = new CommentMapper();
        service = new CommentServiceImpl(commentRepository,
                userRepository,
                bookingRepository,
                itemRepository,
                mapper);
    }

    @Test
    public void createNewComment_whenUserWasNotFound_thenThrowException() {
        long itemId = 1L;
        long authorId = 1L;
        CommentCreateDto createDto = CommentCreateDto.builder()
                .text("text")
                .build();
        String expectedResponse = "Пользователь с id = 1 не найден";
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(PostCommentException.class,
                () -> service.createNewComment(createDto, itemId, authorId));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createNewComment_whenItemWasNotFound_thenThrowException() {
        long itemId = 1L;
        User user = User.builder()
                .id(1L)
                .build();
        CommentCreateDto createDto = CommentCreateDto.builder()
                .text("text")
                .build();
        String expectedResponse = "Объект с id = 1 не найден";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(PostCommentException.class,
                () -> service.createNewComment(createDto, itemId, user.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createComment_whenBookingWasNotFound_thenThrowException() {
        Item item = Item.builder()
                .id(1L)
                .build();
        User user = User.builder()
                .id(1L)
                .build();
        CommentCreateDto createDto = CommentCreateDto.builder()
                .text("text")
                .build();
        String expectedResponse = "Бронь с такими данными не найдена";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        Throwable throwable = assertThrows(PostCommentException.class,
                () -> service.createNewComment(createDto, item.getId(), user.getId()));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createNewComment_whenDataIsCorrect_thenReturnNewCommentDto() {
        LocalDateTime now = LocalDateTime.now();
        Item item = Item.builder()
                .id(1L)
                .build();
        User user = User.builder()
                .name("author")
                .id(1L)
                .build();
        CommentCreateDto createDto = CommentCreateDto.builder()
                .text("text")
                .build();
        Booking booking = Booking.builder().id(1L).build();
        Comment result = Comment.builder()
                .id(1L)
                .created(now)
                .text("text")
                .author(user)
                .item(item)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findFirstByBookerIdAndItemIdAndEndBefore(anyLong(),
                        anyLong(),
                        any(LocalDateTime.class),
                        any(Sort.class))).thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(result);

        CommentDto dto = service.createNewComment(createDto, item.getId(), user.getId());

        assertEquals(1L, dto.getId());
        assertEquals(createDto.getText(), dto.getText());
        assertEquals(now, dto.getCreated());
        assertEquals(user.getName(), dto.getAuthorName());
    }
}