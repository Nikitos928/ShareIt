package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Value
public class CommentDto {
    long id;
    String text;
    String authorName;
    LocalDateTime created;
}
