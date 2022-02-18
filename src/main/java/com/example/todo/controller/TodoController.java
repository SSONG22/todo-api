package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.dto.TodoRequest;
import com.example.todo.response.ErrorCode;
import com.example.todo.response.exception.BusinessException;
import com.example.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/todos")
@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/{todoId}")
    public TodoDto getTodo(@PathVariable("todoId") Long todoId) {
        return todoService.getTodo(todoId);
    }

    @PutMapping("/{todoId}")
    public TodoDto updateTodo(@PathVariable Long todoId,
                              @RequestBody TodoRequest request,
                              @RequestHeader(value = "apikey",required = false) String apikey) {
        valid(apikey);
        return todoService.updateTodo(todoId, request);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{todoId}")
    public void deleteTodo(@PathVariable Long todoId, @RequestHeader(value = "apikey", required = false) String apikey) {
        valid(apikey);
        todoService.deleteTodo(todoId);
    }

    @PostMapping
    public TodoDto createTodo(@RequestBody TodoRequest request, @RequestHeader(value = "apikey", required = false) String apikey) {
        valid(apikey);
        return todoService.createTodo(request);
    }

    @GetMapping
    public List<TodoListDto> listTodos(@RequestParam("skip") int skip, @RequestParam("limit") int limit) {
        return todoService.getTodos(skip, limit);
    }


    private void valid(String apikey) {
        if (apikey == null || apikey.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }
}
