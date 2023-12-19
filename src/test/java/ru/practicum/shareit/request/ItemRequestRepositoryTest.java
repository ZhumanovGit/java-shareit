package ru.practicum.shareit.request;

import org.h2.engine.UserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void findAllByOwnerId_whenDbIsFilled_thenReturnListOfRequests() {
        long ownerId = 1L;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        User user = User.builder().id(1L).name("test").email("test@com").build();
        ItemRequest firstRequest = ItemRequest.builder()
                .id(1L)
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();
        ItemRequest secondRequest = ItemRequest.builder()
                .id(2L)
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();
        ItemRequest thirdRequest = ItemRequest.builder()
                .id(3L)
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();
        entityManager.merge(user);
        entityManager.merge(firstRequest);
        entityManager.merge(secondRequest);
        entityManager.merge(thirdRequest);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwnerId(ownerId, sort);

        assertEquals(3, itemRequests.size());
    }

    @Test
    public void findALlByOwnerId_whenDbIsEmpty_thenReturnEmptyList() {

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwnerId(1L, Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(0, itemRequests.size());
    }

    @Test
    public void findAllByOwnerIdNot_whenDbIsFilled_thenReturnPageOfRequests() {
        long ownerId = 1L;
        User wrondUser = User.builder().id(1L).name("wrong").email("wrong@com").build();
        User user = User.builder().id(2L).name("test").email("test@com").build();
        ItemRequest firstRequest = ItemRequest.builder()
                .id(1L)
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();
        ItemRequest secondRequest = ItemRequest.builder()
                .id(2L)
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();
        ItemRequest thirdRequest = ItemRequest.builder()
                .id(3L)
                .owner(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();
        entityManager.merge(wrondUser);
        entityManager.merge(user);
        entityManager.merge(firstRequest);
        entityManager.merge(secondRequest);
        entityManager.merge(thirdRequest);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByOwnerIdNot(ownerId, pageable);

        assertEquals(3, itemRequestsPage.getTotalElements());
    }

    @Test
    public void findAllByOwnerIdNot_whenDbIsEmpty_thenReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByOwnerIdNot(1L, pageable);

        assertEquals(0, itemRequestsPage.getTotalElements());
    }
}