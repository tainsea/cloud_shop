package cn.fourse.stockservice.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Map<String, Object> handle(
            BusinessException e) {

        Map<String, Object> result =
                new HashMap<>();

        result.put("code", 500);
        result.put(
                "message",
                e.getMessage()
        );

        return result;
    }
}