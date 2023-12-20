package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(ItemCreateDto dto, long ownerId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        Long requestId = dto.getRequestId();
        if (requestId != null) {
            requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запроса с id = " + requestId + " не существует"));
        }

        Item item = mapper.itemCreateDtoToItem(dto);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);
        return mapper.itemToItemDto(createdItem);
    }

    @Override
    @Transactional
    public ItemDto patchItem(@NonNull ItemUpdateDto itemUpdates, long itemId, long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект с id = " + itemId + " не найден"));

        if (item.getOwner().getId() != ownerId) {
            throw new NotFoundException("Не найден данный объект у данного пользователя");
        }

        if (itemUpdates.getName() == null) {
            itemUpdates.setName(item.getName());
        }

        if (itemUpdates.getDescription() == null) {
            itemUpdates.setDescription(item.getDescription());
        }

        if (itemUpdates.getAvailable() == null) {
            itemUpdates.setAvailable(item.getAvailable());
        }

        Item itemForUpdate = mapper.itemUpdateDtoToItem(itemUpdates);
        itemForUpdate.setId(itemId);
        itemForUpdate.setOwner(item.getOwner());

        itemRepository.save(itemForUpdate);
        return mapper.itemToItemDto(itemForUpdate);

    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long id, long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + requesterId + " не найден"));

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("объект с id = " + id + " не найден"));
        ItemDto itemDto = mapper.itemToItemDto(item);

        List<Comment> comments = commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"));
        if (!comments.isEmpty()) {
            itemDto.setComments(comments.stream()
                    .map(commentMapper::commentToCommentDto)
                    .collect(Collectors.toList()));
        }

        if (requesterId != item.getOwner().getId()) {
            return itemDto;
        }

        LocalDateTime now = LocalDateTime.now();
        Booking previousBooking = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIs(item.getId(),
                        now,
                        BookingStatus.APPROVED,
                        Sort.by(Sort.Direction.DESC, "start"))
                .orElse(null);
        itemDto.setLastBooking(null);
        if (previousBooking != null) {
            itemDto.setLastBooking(bookingMapper.bookingToItemBookingDto(previousBooking));
        }

        Booking futureBooking = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNot(item.getId(),
                        now,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.ASC, "start"))
                .orElse(null);
        itemDto.setNextBooking(null);
        if (futureBooking != null) {
            itemDto.setNextBooking(bookingMapper.bookingToItemBookingDto(futureBooking));
        }

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemInfoDto> getItemsByOwnerId(long ownerId, int from, int size) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        int page = 0;
        if (from >= size) {
            page = (from + 1) % size == 0 ? ((from + 1) / size) - 1 : (from + 1) / size;
        }
        Page<Item> items = itemRepository.findAllByOwnerId(ownerId, PageRequest.of(page, size));
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        BooleanExpression lastBookingsExpression = QBooking.booking.item.id.in(itemIds)
                .and(QBooking.booking.start.before(now))
                .and(QBooking.booking.status.eq(BookingStatus.APPROVED));
        List<Booking> previousBookings = StreamSupport.stream(bookingRepository
                        .findAll(lastBookingsExpression).spliterator(), false)
                .collect(Collectors.toList());
        BooleanExpression nextBookingsExpression = QBooking.booking.item.id.in(itemIds)
                .and(QBooking.booking.start.after(now))
                .and(QBooking.booking.status.ne(BookingStatus.REJECTED));
        List<Booking> futureBookings = StreamSupport.stream(bookingRepository
                        .findAll(nextBookingsExpression).spliterator(), false)
                .collect(Collectors.toList());
        List<Comment> allComments = commentRepository.findAllByItemIdIn(itemIds,
                Sort.by(Sort.Direction.ASC, "created"));

        Map<Long, Booking> itemsLastBookings = new HashMap<>();
        for (Booking booking : previousBookings) {
            Long itemId = booking.getItem().getId();
            Booking oldBooking = itemsLastBookings.get(itemId);
            if (oldBooking == null) {
                itemsLastBookings.put(itemId, booking);
                continue;
            }
            if (oldBooking.getStart().isBefore(booking.getStart())) {
                itemsLastBookings.put(itemId, booking);
            }
        }
        Map<Long, Booking> itemsNextBookings = new HashMap<>();
        for (Booking booking : futureBookings) {
            Long itemId = booking.getItem().getId();
            Booking oldBooking = itemsNextBookings.get(itemId);
            if (oldBooking == null) {
                itemsNextBookings.put(itemId, booking);
                continue;
            }
            if (oldBooking.getStart().isAfter(booking.getStart())) {
                itemsNextBookings.put(itemId, booking);
            }
        }

        Map<Long, List<Comment>> itemCommentsMap = new HashMap<>();
        for (Comment comment : allComments) {
            itemCommentsMap.computeIfAbsent(comment.getItem().getId(), k -> new ArrayList<>()).add(comment);
        }

        List<ItemInfoDto> result = new ArrayList<>();
        for (Item item : items) {
            Long itemId = item.getId();
            Booking lastBooking = itemsLastBookings.get(itemId);
            Booking nextBooking = itemsNextBookings.get(itemId);
            List<Comment> itemComments = itemCommentsMap.getOrDefault(itemId, new ArrayList<>());
            result.add(mapper.itemToItemInfoDto(item, itemComments, nextBooking, lastBooking));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByNameOrDesc(String substring, int from, int size) {
        if (substring.isBlank()) {
            return new ArrayList<>();
        }
        String needSubstring = substring.toLowerCase();
        int page = 0;
        if (from >= size) {
            page = from % size == 0 ? (from / size) - 1 : from / size;
        }
        Page<Item> items = itemRepository.findAllByNameOrDesc(needSubstring, PageRequest.of(page, size));
        return items.stream()
                .filter(Item::getAvailable)
                .map(mapper::itemToItemDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void deleteItemById(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteItems() {
        itemRepository.deleteAll();
    }
}
