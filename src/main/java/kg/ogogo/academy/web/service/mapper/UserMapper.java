package kg.ogogo.academy.web.service.mapper;

import kg.ogogo.academy.web.domain.security.UserGroup;
import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public List<UserDTO> usersToUserDTOs(List<User> users) {
        return users.stream()
                .filter(Objects::nonNull)
                .map(this::userToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO userToUserDTO(User user) {
        return new UserDTO(user);
    }

    public List<User> userDTOsToUsers(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::userDTOToUser)
                .collect(Collectors.toList());
    }

    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            User user = new User();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setActivated(userDTO.isActivated());
             //todo refactor
            Set<UserGroup> authorities = this.authoritiesFromStrings(userDTO.getUserGroups().stream()
                    .map(UserGroup::getCode).collect(Collectors.toSet())
            );
            user.setUserGroups(authorities);
            return user;
        }
    }


    private Set<UserGroup> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<UserGroup> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities = authoritiesAsString.stream().map(string -> {
                UserGroup auth = new UserGroup();
                auth.setCode(string);
                return auth;
            }).collect(Collectors.toSet());
        }

        return authorities;
    }

    public User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
