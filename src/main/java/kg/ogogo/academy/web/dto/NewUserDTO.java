package kg.ogogo.academy.web.dto;

import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.domain.security.UserGroup;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    //  @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private Set<String> userGroups;



    public static NewUserDTO map(User user){
        return NewUserDTO.builder()
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userGroups(user.getUserGroups().stream().map(UserGroup::getCode).collect(Collectors.toSet()))
                .build();
    }

}
