package com.example.todo.service;

import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.response.ErrorCode;
import com.example.todo.response.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Value("${todoUrl}: http://localhost:8080/todos/")
    private String baseUrl;

    @Transactional(readOnly = true)
    public TodoDto getTodo(Long todoId) {
        return TodoDto.from(
                todoRepository.findById(todoId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND)));
    }

    public TodoDto updateTodo(Long todoId, TodoRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        todo.update(request);
        return TodoDto.from(todo);
    }

    public void deleteTodo(Long todoId) {
        todoRepository.deleteById(todoId);
    }

    public TodoDto createTodo(TodoRequest request) {
        Todo todo = request.toEntity();
        return TodoDto.from(todoRepository.save(todo));
    }

    @Transactional(readOnly = true)
    public List<TodoListDto> getTodos(int skip, int limit) {
        if (limit == 0) {
            limit = (int) todoRepository.count();
        }
        return todoRepository.findAll(PageRequest.of(skip, limit, Sort.Direction.DESC, "id"))
                .getContent()
                .stream()
                .map(todo -> TodoListDto.of(todo, baseUrl + todo.getId()))
                .collect(Collectors.toList());
    }

}
