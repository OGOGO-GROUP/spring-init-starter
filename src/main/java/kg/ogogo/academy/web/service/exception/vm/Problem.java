package kg.ogogo.academy.web.service.exception.vm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Problem {

    private HttpStatus status;


    private String timestamp;

    private String message;

    private String type;

    private String path;

    private Problem() {
        timestamp = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now());
    }

    Problem(HttpStatus status) {
        this();
        this.status = status;
    }

    public Problem(HttpStatus status, Exception ex, WebRequest request) {
        this();
        this.status = status;
        this.message = ex.getMessage();
        this.type = ex.getClass().getSimpleName();
        this.path = ((ServletWebRequest)request).getRequest().getRequestURI();
    }

    public Problem(HttpStatus status, String message, Throwable ex, WebRequest request) {
        this();
        this.status = status;
        this.message = message;
        this.type = ex.getClass().getSimpleName();
        this.path = ((ServletWebRequest)request).getRequest().getRequestURI();
    }

}
