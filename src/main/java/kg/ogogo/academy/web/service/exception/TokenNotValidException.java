package kg.ogogo.academy.web.service.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class TokenNotValidException extends BadCredentialsException {
    public TokenNotValidException() {
        super("Token is invalid, authorize again.");
    }
}
