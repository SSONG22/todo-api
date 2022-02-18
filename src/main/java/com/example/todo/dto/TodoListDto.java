package com.example.todo.dto;

import com.example.todo.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TodoListDto {
    private Long id;
    private String name;
    private boolean completed;
    private LocalDateTime completedAt;
    private String url;

    public static TodoListDto of(Todo todo, String url) {
        return TodoListDto.builder()
                .id(todo.getId())
                .name(todo.getName())
                .completed(todo.getCompleted())
                .completedAt(todo.getCompletedAt())
                .url(url)
                .build();
    }
}
