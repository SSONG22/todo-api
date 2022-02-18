package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.dto.TodoRequest;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.todo.ApiDocumentUtils.getDocumentRequest;
import static com.example.todo.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(TodoController.class)
@AutoConfigureRestDocs
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTodo() throws Exception {
        // given
        TodoDto response = TodoDto.builder()
                .id(1l)
                .name("test name")
                .completed(true)
                .completedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(todoService.getTodo(1l)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(
                get("/todos/{todoId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(document("todo/get-todo",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("todoId").description("TODO 아이디")
                        )));
    }

    @Test
    void updateTodo() throws Exception {
        // given
        Long todoId = 1l;
        TodoDto response = TodoDto.builder()
                .id(todoId)
                .name("test name")
                .completed(false)
                .completedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(todoService.updateTodo(eq(1l), any(TodoRequest.class)))
                .willReturn(response);

        // when
        TodoRequest request = new TodoRequest("update name", true);
        ResultActions result = mockMvc.perform(
                put("/todos/{todoId}", todoId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("apikey", 123)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(document("todo/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("todoId").description("TODO 아이디")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("TODO 이름"),
                                fieldWithPath("completed").type(JsonFieldType.BOOLEAN).description("TODO 완료 여부")
                        )
                ));
    }

    @Test
    void deleteTodo() throws Exception {
        // given
        Long todoId = 1l;

        // when
        ResultActions result = mockMvc.perform(
                delete("/todos/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("apikey", 123)
        );

        // then
        result.andExpect(status().is2xxSuccessful())
                .andDo(document("todo/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("todoId").description("TODO 아이디")
                        )
                ));
    }

    @Test
    void createTodo() throws Exception {
        // given
        Long todoId = 1l;
        TodoDto response = TodoDto.builder()
                .id(todoId)
                .name("test name")
                .completed(false)
                .completedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(todoService.createTodo(any(TodoRequest.class)))
                .willReturn(response);

        // when
        TodoRequest request = new TodoRequest("test name", false);
        ResultActions result = mockMvc.perform(
                post("/todos")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("apikey", 123)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(document("todo/create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("TODO 이름"),
                                fieldWithPath("completed").type(JsonFieldType.BOOLEAN).description("TODO 완료 여부")
                        )
                ));
    }

    @Test
    void listTodos() throws Exception {
        // given
        int skip = 0;
        int limit = 10;
        List<TodoListDto> response = makeFixture();
        given(todoService.getTodos(skip, limit))
                .willReturn(response);

        // when
        TodoRequest request = new TodoRequest("test name", false);
        ResultActions result = mockMvc.perform(
                get("/todos")
                        .param("skip", String.valueOf(skip))
                        .param("limit", String.valueOf(limit))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(document("todo/get-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("skip").description("skip"),
                                parameterWithName("limit").description("limit")
                        )
                ));
    }

    private List<TodoListDto> makeFixture() {
        List<TodoListDto> listDtos = new ArrayList<>();
        for (long i = 10; i > 0; i--) {
            TodoListDto response = TodoListDto.builder()
                    .id(i)
                    .name("test name")
                    .completed(false)
                    .completedAt(null)
                    .url("http://localhost:8080/todos/" + i)
                    .build();
            listDtos.add(response);
        }
        return listDtos;
    }
}