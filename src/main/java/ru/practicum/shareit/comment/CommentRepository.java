package ru.practicum.shareit.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findAllByItemId(Long itemId, Sort sort);

    void deleteAllByAuthorId(Long authorId);
}
