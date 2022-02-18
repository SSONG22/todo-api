package com.example.todo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String error;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = String.valueOf(errorCode.getStatus());
        this.error = errorCode.getMessage();
    }
}
