package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.IncorrectAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) throws NoSuchMethodException {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
         .getFieldErrors()
         .forEach(error -> errors.put(error.getField(),
                                               error.getDefaultMessage()
         ));

        //Получение значения сообщения по умолчанию аннотации NotDuplicateEmail для исключения хардкода
        Method messageMethod = NotDuplicateEmail.class.getMethod("message");
        String duplicateEmailDefaultMessage = (String) messageMethod.getDefaultValue();

        if (errors.containsValue(duplicateEmailDefaultMessage)) {
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Не найден элемент", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleIncorrectAccessException(final IncorrectAccessException e) {
        return Map.of("В доступе отказано", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.warn("Отсутствует обязательный заголовок {}", e.getHeaderName());
        return Map.of("Отсутствует обязательный заголовок", e.getMessage());
    }
}
