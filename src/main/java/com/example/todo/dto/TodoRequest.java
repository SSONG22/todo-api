package com.example.todo.dto;

import com.example.todo.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {
    private String name;
    private Boolean completed;

    public Todo toEntity() {
        return Todo.builder()
                .name(this.name)
                .completed(this.completed)
                .completedAt(this.completed ? LocalDateTime.now() : null)
                .build();
    }
}
