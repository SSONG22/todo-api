package com.example.todo.entity;

import com.example.todo.dto.TodoRequest;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Todo {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Boolean completed;

    private LocalDateTime completedAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void update(TodoRequest updatedTodo) {
        this.name = updatedTodo.getName();
        this.completed = updatedTodo.getCompleted();
        if (completed) {
            completedAt = LocalDateTime.now();
        }
    }
}
