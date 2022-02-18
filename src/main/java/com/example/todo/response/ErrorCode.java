package com.example.todo.response;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404,  "Not Found"),
    UNAUTHORIZED(401, "Not Authorized"),
    INTERNAL_SERVER_ERROR(500, "Server Error");

    private final String message;
    private final int status;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
}
