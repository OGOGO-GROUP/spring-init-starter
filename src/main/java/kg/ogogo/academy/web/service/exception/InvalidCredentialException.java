package kg.ogogo.academy.web.service.exception;

import org.springframework.security.authentication.BadCredentialsException;


public class InvalidCredentialException extends BadCredentialsException{

    private static final long serialVersionUID = 1L;

    public InvalidCredentialException() {
        super("Invalid credentials.");
    }
}
