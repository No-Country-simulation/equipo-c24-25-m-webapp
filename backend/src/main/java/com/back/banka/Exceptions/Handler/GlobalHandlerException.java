package com.back.banka.Exceptions.Handler;


import com.back.banka.Exceptions.Custom.*;
import com.back.banka.Exceptions.Dtos.ErrorResponseDto;
import com.back.banka.Exceptions.Dtos.ErrorsResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalHandlerException {

    //lanzadores

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponseDto> userNotFoundHandlerException(UserNotFoundException exception){
        ErrorResponseDto  errorResponseDto = ErrorResponseDto.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .dateCreation(LocalDate.now())
                .build();

        return new ResponseEntity<>(errorResponseDto,HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidCredentialExceptions.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(InvalidCredentialExceptions exception){
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .dateCreation(LocalDate.now())
                .build();

        return new ResponseEntity<>(errorResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(CustomAuthenticationException exception){
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .dateCreation(LocalDate.now())
                .build();

        return new ResponseEntity<>(errorResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .dateCreation(LocalDate.now())
                .build();

        return new ResponseEntity<>(errorResponseDto, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(BadRequestExceptions.class)
    public ResponseEntity<ErrorResponseDto> handleBanRequestException(BadRequestExceptions ex) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .dateCreation(LocalDate.now())
                .build();

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorsResponse errorResponse = new ErrorsResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "La solicitud contiene errores de validación. Por favor, revisa los campos indicados.",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getReason());
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }



    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .dateCreation(LocalDate.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


}
