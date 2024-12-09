package com.work_service.work.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ServiceExceptionCode implements ErrorCode {

    DATA_NOT_FOUND(HttpStatus.NO_CONTENT, "not found user/book"),
    NOT_ALLOW_BOOK(HttpStatus.FORBIDDEN, "not allow book"),
    ALREADY_JOIN(HttpStatus.CONFLICT, "already join userId"),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

}
