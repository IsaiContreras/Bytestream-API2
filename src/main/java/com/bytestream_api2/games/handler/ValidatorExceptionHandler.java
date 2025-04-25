package com.bytestream_api2.games.handler;

import com.bytestream_api2.games.utilities.BodyFormatter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class ValidatorExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException manve,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> fieldErrors = manve
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map((DefaultMessageSourceResolvable::getDefaultMessage))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BodyFormatter.error(String.join(" || ", fieldErrors)));
    }

}
