package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.dto.TodoRequest;
import com.example.todo.response.ErrorCode;
import com.example.todo.response.exception.BusinessException;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
class TodoControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("todo 조회시, 없는 아이디를 조회하면 404에러가 발생한다.")
    @Test
    void getTodo_404() throws Exception {
        // given
        Long testId = 10000l;
        given(todoService.getTodo(testId)).willThrow(new BusinessException(ErrorCode.NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(
                get("/todos/{todoId}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().is(404))
                .andDo(document("todo/get-todo/error/404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("todoId").description("TODO 아이디")
                        )));
    }

    @DisplayName("todo 수정시 apikey가 없으면 401에러가 난다.")
    @Test
    void updateTodo_401() throws Exception {
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
        );

        // then
        result.andExpect(status().is(401))
                .andDo(document("todo/update/error",
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

    @DisplayName("todo 삭제시 apikey가 없으면 401에러가 난다.")
    @Test
    void deleteTodo_401() throws Exception {
        // given
        Long todoId = 1l;

        // when
        ResultActions result = mockMvc.perform(
                delete("/todos/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().is(401))
                .andDo(document("todo/delete/error",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("todoId").description("TODO 아이디")
                        )
                ));
    }

    @DisplayName("todo 생성시 apikey가 없으면 401에러가 난다.")
    @Test
    void createTodo_401() throws Exception {
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
        );

        // then
        result.andExpect(status().is(401))
                .andDo(document("todo/create/error",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("TODO 이름"),
                                fieldWithPath("completed").type(JsonFieldType.BOOLEAN).description("TODO 완료 여부")
                        )
                ));
    }

    @DisplayName("todo list 조회시 알 수 없는 에러 발생시 500 서버 에러가 난다.")
    @Test
    void listTodos_500() throws Exception {
        // given
        int skip = 0;
        int limit = 10;
        given(todoService.getTodos(skip, limit))
                .willThrow(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
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
        result.andExpect(status().isInternalServerError())
                .andDo(document("todo/get-list/error/500",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("skip").description("skip"),
                                parameterWithName("limit").description("limit")
                        )
                ));
    }


    @DisplayName("todo 조회시 요청값이 잘못되면, 404 exception 이 발생한다.")
    @Test
    void getTodos_400() throws Exception {
        // given
        // when
        ResultActions result = mockMvc.perform(
                get("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().is(400))
                .andDo(document("todo/list-todo/error/400",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }


}