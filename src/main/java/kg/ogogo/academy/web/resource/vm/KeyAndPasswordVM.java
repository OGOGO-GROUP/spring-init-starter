package kg.ogogo.academy.web.resource.vm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class KeyAndPasswordVM {

    private String key;

    @Min(value = 8)
    private String newPassword;

}
