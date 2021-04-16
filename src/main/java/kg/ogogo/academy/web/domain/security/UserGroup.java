package kg.ogogo.academy.web.domain.security;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "og_user_group")
@Getter
@Setter
public class  UserGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 50)
    @Id
    @Column(length = 50)
    private String code;

    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGroup)) {
            return false;
        }
        return Objects.equals(code, ((UserGroup) o).code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Authority{" +
                "name='" + code + '\'' +
                "}";
    }
}
