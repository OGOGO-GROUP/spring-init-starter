package kg.ogogo.academy.web.config;

public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String ANONYMOUS_USER = "anonymoususer";

    public static final Short DRIVER = 0;

    private Constants() {
    }
}
