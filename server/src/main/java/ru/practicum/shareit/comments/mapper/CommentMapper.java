package ru.practicum.shareit.comments.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public Comment toEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }
}