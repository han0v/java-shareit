package ru.practicum.shareit.comments.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}