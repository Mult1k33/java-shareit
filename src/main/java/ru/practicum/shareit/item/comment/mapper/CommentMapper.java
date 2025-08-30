package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentNewDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

public class CommentMapper {

    // Преобразование Comment в Dto
    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    // Преобразование CommentNewDto в Comment
    public static Comment mapToComment(CommentNewDto commentNewDto, Item item, User author) {
        return Comment.builder()
                .text(commentNewDto.getText())
                .item(item)
                .author(author)
                .created(Instant.now())
                .build();
    }
}