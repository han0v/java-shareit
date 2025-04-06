package ru.practicum.shareit.comments.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void toDto() {
        User author = new User();
        author.setName("John Doe");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a comment");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2023, 10, 1, 12, 0));
        CommentDto commentDto = CommentMapper.toDto(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void toEntity() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("This is a comment");
        commentDto.setCreated(LocalDateTime.of(2023, 10, 1, 12, 0));
        Comment comment = CommentMapper.toEntity(commentDto);
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }
}