package kg.ogogo.academy.web.web.rest.handler;

import javassist.NotFoundException;
import kg.ogogo.academy.web.service.exception.*;
import kg.ogogo.academy.web.service.exception.vm.Problem;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler implements AdviceTrait {

 /*   @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Bad Request";
        return buildResponseEntity(new Problem(HttpStatus.BAD_REQUEST, error, ex, request));
    }*/

    private ResponseEntity<Object> buildResponseEntity(Problem apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InvalidCredentialException.class)
    protected ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialException ex, WebRequest request) {
        return buildResponseEntity(new Problem(HttpStatus.UNAUTHORIZED, ex, request));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest request){
        return buildResponseEntity(new Problem(HttpStatus.UNAUTHORIZED, ex, request));
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    protected ResponseEntity<Object> handleUsernameAlreadyUsedException(UsernameAlreadyUsedException ex, WebRequest request){
        return buildResponseEntity(new Problem(HttpStatus.NOT_ACCEPTABLE, ex, request));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
        return buildResponseEntity(new Problem(HttpStatus.NOT_FOUND, ex, request));
    }

    @ExceptionHandler(UserNotActivatedException.class)
    protected ResponseEntity<Object> handleUserNotActivatedException(UserNotFoundException ex, WebRequest request){
        return buildResponseEntity(new Problem(HttpStatus.UNAUTHORIZED, ex, request));
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    protected ResponseEntity<Object> handleRedisConnectionFailureException(RedisConnectionFailureException ex, WebRequest request){
        return buildResponseEntity(new Problem(HttpStatus.INTERNAL_SERVER_ERROR, ex, request));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request){
        return buildResponseEntity(new Problem(HttpStatus.NOT_FOUND, ex, request));
    }







}
