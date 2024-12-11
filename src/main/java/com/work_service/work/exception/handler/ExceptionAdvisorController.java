package com.work_service.work.exception.handler;

import com.work_service.work.domain.response.CommonResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvisorController {

    @ExceptionHandler(Exception.class)
    CommonResponse processExceptionError(Exception e) {
        if(e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            return new CommonResponse(206, fieldError.getDefaultMessage(), null);
        } else {
            return new CommonResponse(206, e.getMessage(), null);
        }
    }
}
