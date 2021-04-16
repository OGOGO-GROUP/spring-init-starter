package kg.ogogo.academy.web.service.exception;

public class EmailAlreadyUsedException extends Exception {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super("Email already used.");
    }

}
