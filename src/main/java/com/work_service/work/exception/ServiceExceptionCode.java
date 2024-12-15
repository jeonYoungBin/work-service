package com.work_service.work.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ServiceExceptionCode implements ErrorCode {

    DATA_NOT_FOUND_USER(HttpStatus.NO_CONTENT, "not found user"),
    DATA_NOT_FOUND_BOOK(HttpStatus.NO_CONTENT, "not found book"),
    NOT_ALLOW_BOOK(HttpStatus.FORBIDDEN, "not allow book"),
    ALREADY_JOIN(HttpStatus.CONFLICT, "already join userId"),
    NOT_PASSWORD_MATCH(HttpStatus.NOT_FOUND, "not match password"),
    ALREADY_VIEW(HttpStatus.NOT_FOUND, "already view"),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

}
