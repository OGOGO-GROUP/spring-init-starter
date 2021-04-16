package kg.ogogo.academy.web.service.exception;

import javassist.NotFoundException;

import java.util.function.Supplier;

public class UserNotFoundException extends NotFoundException implements Supplier<UserNotFoundException> {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username) {
        super(String.format("User with parameter %s not found.", username));
    }

    @Override
    public UserNotFoundException get() {
        return null;
    }
}
