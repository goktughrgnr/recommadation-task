package com.goktug.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
// Tum controller'larda olusan hatalari merkezi olarak yakalar
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    // Path/query parametre tipi hataliysa 400 Bad Request doner
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        // Beklenen Java tipini guvenli sekilde alir
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        // Kullaniciya anlasilir bir hata mesaji hazirlar
        String message = String.format("'%s' parameter has invalid value: '%s'. Expected type: %s",
                ex.getName(),
                ex.getValue(),
                expectedType);

        // JSON hata govdesini key-value seklinde olusturur
        Map<String, String> error = Map.of(
                "error", "Bad Request",
                "message", message);
        // HTTP 400 + hata mesaji dondurur
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    // Is kurallarindan gelen RuntimeException'lari 404 olarak dondurur
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        Map<String, String> error = Map.of(
                "error", "Not Found",
                "message", ex.getMessage());
        // HTTP 404 + hata mesaji dondurur
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}