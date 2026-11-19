package hr.lektie.webapi.exceptions;

import hr.lektie.webapi.dto.ResponseObj;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Turns exceptions thrown by controllers into ResponseObj bodies.
 * Access-denied (403) is not handled here on purpose: Spring Security answers it via JsonSecurityErrorHandler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Same message for unknown user and wrong password, so the API does not reveal which usernames exist.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseObj<Void>> handleAuthentication(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObj<>("Invalid credentials"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObj<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ResponseObj<>(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseObj<Void>> handleUnreadableBody(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ResponseObj<>("Malformed request body"));
    }
}
