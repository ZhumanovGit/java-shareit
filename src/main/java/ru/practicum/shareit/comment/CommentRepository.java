package ru.practicum.shareit.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId, Sort sort);

    List<Comment> findAllByItemIdIn(List<Long> ids, Sort sort);

    void deleteAllByAuthorId(Long authorId);
}
