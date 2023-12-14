package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    @Sql({"/item-request-storage-test.sql"})
    public void findAllByOwnerId_whenDbIsFilled_thenReturnListOfRequests() {
        long ownerId = 2L;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwnerId(ownerId, sort);

        assertEquals(2, itemRequests.size());
        assertEquals(3, itemRequests.get(0).getId());
        assertEquals(1, itemRequests.get(1).getId());
    }

    @Test
    public void findALlByOwnerId_whenDbIsEmpty_thenReturnEmptyList() {

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwnerId(1L, Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(0, itemRequests.size());
    }

    @Test
    @Sql({"/item-request-storage-test.sql"})
    public void findAllByOwnerIdNot_whenDbIsFilled_thenReturnPageOfRequests() {
        long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByOwnerIdNot(ownerId, pageable);

        assertEquals(2, itemRequestsPage.getTotalElements());
    }

    @Test
    public void findAllByOwnerIdNot_whenDbIsEmpty_thenReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByOwnerIdNot(1L, pageable);

        assertEquals(0, itemRequestsPage.getTotalElements());
    }
}