package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CreatedCommentDto;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ManyCreatedItemsDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CreatedItemDto createItem(ItemDto dto, long ownerId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        Item item = mapper.itemDtoToItem(dto);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);
        return mapper.itemToCreatedItemDto(createdItem);
    }

    @Override
    @Transactional
    public CreatedItemDto patchItem(@NonNull UpdateItemDto itemUpdates, long itemId, long ownerId) {
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

        Item itemForUpdate = mapper.updateItemDtoToItem(itemUpdates);
        itemForUpdate.setId(itemId);
        itemForUpdate.setOwner(item.getOwner());

        itemRepository.save(itemForUpdate);
        return mapper.itemToCreatedItemDto(itemForUpdate);

    }

    @Override
    @Transactional(readOnly = true)
    public CreatedItemDto getItemById(long id, long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + requesterId + " не найден"));

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("объект с id = " + id + " не найден"));
        CreatedItemDto itemDto = mapper.itemToCreatedItemDto(item);

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId());
        if (!comments.isEmpty()) {
            itemDto.setComments(comments.stream()
                    .map(commentMapper::commentToCreatedCommentDto)
                    .collect(Collectors.toList()));
        }

        if (requesterId != item.getOwner().getId()) {
            return itemDto;
        }

        LocalDateTime now = LocalDateTime.now();
        Booking previousBooking = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusIsOrderByStartDesc(item.getId(), now, BookingStatus.APPROVED)
                .orElse(null);

        itemDto.setLastBooking(bookingMapper.bookingToItemBookingDto(previousBooking));

        Booking futureBooking = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusIsNotOrderByStartAsc(item.getId(), now, BookingStatus.REJECTED)
                .orElse(null);

        itemDto.setNextBooking(bookingMapper.bookingToItemBookingDto(futureBooking));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManyCreatedItemsDto> getItemsByOwnerId(long ownerId) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        List<ManyCreatedItemsDto> items = mapper.itemToManyItemsDto(itemRepository.findAllByOwnerId(ownerId));
        List<Long> itemIds = items.stream()
                .map(ManyCreatedItemsDto::getId)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        List<Booking> previousBookings = bookingRepository
                .findAllByItemIdInAndStartBeforeAndStatusIs(itemIds, now, BookingStatus.APPROVED);
        List<Booking> futureBookings = bookingRepository
                .findAllByItemIdInAndStartAfterAndStatusIsNot(itemIds, now, BookingStatus.REJECTED);
        List<ManyCreatedItemsDto> itemsWithBookings = items.stream()
                .peek(item -> {
                    Booking lastBooking = previousBookings.stream()
                            .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                            .max(Comparator.comparing(Booking::getStart))
                            .orElse(null);
                    if (lastBooking != null) {
                        item.setLastBooking(bookingMapper.bookingToItemBookingDto(lastBooking));
                    } else {
                        item.setLastBooking(null);
                    }

                    Booking next = futureBookings.stream()
                            .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);
                    if (next != null) {
                        item.setNextBooking(bookingMapper.bookingToItemBookingDto(next));
                    } else {
                        item.setLastBooking(null);
                    }
                })
                .collect(Collectors.toList());


        return itemsWithBookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreatedItemDto> getItemsByNameOrDesc(String substring) {
        if (substring.isBlank()) {
            return new ArrayList<>();
        }
        String needSubstring = substring.toLowerCase();

        List<Item> items = itemRepository.findAllByNameOrDesc(needSubstring);
        return items.stream()
                .filter(Item::getAvailable)
                .map(mapper::itemToCreatedItemDto)
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
