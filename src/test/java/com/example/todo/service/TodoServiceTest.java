package com.example.todo.service;

import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.response.ErrorCode;
import com.example.todo.response.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    private Todo findTodo;
    private Long testId;
    private TodoRequest request;

    @Test
    void todo_조회() {
        // given
        testId = 1l;
        TodoRequest request = new TodoRequest("todoName", false);
        findTodo = request.toEntity();
        given(todoRepository.findById(testId)).willReturn(Optional.ofNullable(findTodo));

        // when
        TodoDto result = todoService.getTodo(testId);
        // then
        assertThat(result.getId()).isEqualTo(findTodo.getId());
        assertThat(result.getName()).isEqualTo(findTodo.getName());
        assertThat(result.getCompleted()).isEqualTo(findTodo.getCompleted());
        assertThat(result.getCreatedAt()).isEqualTo(findTodo.getCreatedAt());
    }

    @DisplayName("없는 todo id에 대해 Not Found 에러를 던진다.")
    @Test
    void todo_조회_throw() {
        // given
        given(todoRepository.findById(2l)).willThrow(new BusinessException(ErrorCode.NOT_FOUND));

        assertThatThrownBy(() -> todoService.getTodo(2l))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void todo_수정() {
        // given
        testId = 1l;
        request = new TodoRequest("todoName", false);
        findTodo = request.toEntity();
        given(todoRepository.findById(testId)).willReturn(Optional.ofNullable(findTodo));
        request = new TodoRequest("update", true);
        // when
        TodoDto updateTodo = todoService.updateTodo(testId, request);

        // then
        assertThat(updateTodo.getId()).isEqualTo(findTodo.getId());
        assertThat(updateTodo.getCreatedAt()).isEqualTo(findTodo.getCreatedAt());
        assertThat(updateTodo.getUpdatedAt()).isEqualTo(findTodo.getUpdatedAt());
        assertThat(updateTodo.getName()).isEqualTo(findTodo.getName());
        assertThat(updateTodo.getCompleted()).isEqualTo(findTodo.getCompleted());
        assertThat(updateTodo.getCompletedAt()).isEqualTo(findTodo.getCompletedAt());
    }

    @Test
    void todo_생성() {
        // given
        request = new TodoRequest("createTodo", false);
        Todo todo = request.toEntity();
        given(todoRepository.save(todo)).willReturn(todo);

        // when
        TodoDto savedTodo = todoService.createTodo(request);

        // then
        assertThat(savedTodo.getName()).isEqualTo(request.getName());
        assertThat(savedTodo.getName()).isEqualTo(todo.getName());
        assertThat(savedTodo.getCompleted()).isEqualTo(todo.getCompleted());
    }

    @Test
    void todo_삭제() {
        // given
        Long todoId = 1l;
        // when
        todoService.deleteTodo(todoId);
        // then
        verify(todoRepository).deleteById(todoId);
    }

    @Test
    void todo_리스트_가져오기() {
        // given
        List<Todo> todos = makeFixture();
        int limit = 10;
        int offset = 1;
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.DESC, "id");
        Page<Todo> pageTodo = new PageImpl<>(todos.subList(offset * limit, offset * limit + limit), pageable, 50);

        given(todoRepository.findAll(pageable))
                .willReturn(pageTodo);

        // when
        List<TodoListDto> list = todoService.getTodos(offset, limit);

        // then
        assertThat(list.size()).isEqualTo(limit);
    }

    private List<Todo> makeFixture() {
        List<Todo> todos = new ArrayList<>();
        for (long i = 50; i > 0; i--) {
            todos.add(Todo.builder()
                    .id(i)
                    .name("name " + i)
                    .completed(i % 2 == 0)
                    .build());
        }
        return todos;
    }
}