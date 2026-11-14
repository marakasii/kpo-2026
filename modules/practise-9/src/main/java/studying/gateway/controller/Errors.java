package studying.gateway.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import studying.gateway.dto.Api;
import studying.gateway.exception.GatewayException;

@RestControllerAdvice
public class Errors {
  @ExceptionHandler(GatewayException.class)
  public ResponseEntity<Api.Error> business(GatewayException e) {
    return ResponseEntity.status(e.getStatus())
        .body(new Api.Error(e.getCode(), e.getMessage(), e.getRequestId()));
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    HttpMessageNotReadableException.class,
    MissingRequestHeaderException.class,
    MethodArgumentTypeMismatchException.class,
    HandlerMethodValidationException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<Api.Error> badRequest(Exception e) {
    return ResponseEntity.badRequest()
        .body(new Api.Error("INVALID_REQUEST", "Некорректное тело или параметры запроса", null));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Api.Error> conflict(Exception e) {
    return ResponseEntity.status(409)
        .body(
            new Api.Error("DATA_CONFLICT", "Данные уже существуют или нарушают ограничения", null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Api.Error> internal(Exception e) {
    return ResponseEntity.internalServerError()
        .body(new Api.Error("INTERNAL_ERROR", "Внутренняя ошибка сервиса", null));
  }
}
