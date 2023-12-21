package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentMapper mapper;

    @Override
    public CommentDto createNewComment(CommentCreateDto dto,
                                       Long itemId,
                                       Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new PostCommentException("Пользователь с id = " + authorId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new PostCommentException("Объект с id = " + itemId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        bookingRepository
                .findFirstByBookerIdAndItemIdAndEndBefore(authorId,
                        itemId,
                        now,
                        Sort.by(Sort.Direction.DESC, "end"))
                .orElseThrow(() -> new PostCommentException("Бронь с такими данными не найдена"));

        Comment comment = mapper.commentCreateDtoToComment(dto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);
        return mapper.commentToCommentDto(commentRepository.save(comment));
    }
}
