package kg.ogogo.academy.web.resource.rest;

import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.dto.NewUserDTO;
import kg.ogogo.academy.web.dto.UserDTO;
import kg.ogogo.academy.web.service.UserService;
import kg.ogogo.academy.web.service.annotation.GetUser;
import kg.ogogo.academy.web.service.exception.EmailAlreadyUsedException;
import kg.ogogo.academy.web.service.exception.UserNotFoundException;
import kg.ogogo.academy.web.service.exception.UsernameAlreadyUsedException;
import kg.ogogo.academy.web.service.impl.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/users")
@Api(tags = "user resource:", description = " get all users, update user, find user by id, create user")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    private final MailService mailService;

    public UserResource(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @ApiOperation("Get all users with roles; access: ROLE_ADMIN")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public Page<UserDTO> getUsers(
            @ApiIgnore
            @GetUser User user,
            @QuerydslPredicate(root = User.class) Predicate predicate,
            Pageable pageable)
    {

        return userService.getAllUsers(predicate, pageable, user);

    }


    @ApiOperation(value = "Create user; access: ROLE_ADMIN",
        notes="after, user will have email, to put password.")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("create")
    public ResponseEntity<NewUserDTO> addUser(
            @ApiIgnore
            @GetUser User current,
            @RequestBody NewUserDTO userDTO
    ) throws Exception {

        User newUser = userService.addUser(userDTO, current);
        mailService.sendCreationEmail(newUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + "admin" + "-alert", "userManagement");
        try {
            headers.add("X-" + "admin" + "-params", URLEncoder.encode(newUser.getLogin(), StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException ignored) {
        }
        return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(headers)
                .body(NewUserDTO.map(newUser));

    }


    @ApiOperation(value = "Find user by id; access: ROLE_ADMIN")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public UserDTO findByLogin(
            @ApiIgnore
            @GetUser User user,
            @PathVariable("id") Long id
    ) throws UserNotFoundException {
        return new UserDTO(userService.findById(id).orElseThrow(
                new UserNotFoundException(id.toString())
        ), true);
    }



    @ApiOperation(value = "Update user by id; access: ROLE_ADMIN")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(
            @ApiIgnore
            @GetUser User user,
            @PathVariable("id") Long id,
            @Valid @RequestBody UserDTO userDTO
    ) throws UserNotFoundException, EmailAlreadyUsedException, UsernameAlreadyUsedException {

        return ResponseEntity.of(userService.update(userDTO, id));

    }

    @ApiOperation(value = "Get current user by id; access: ANY AUTHENTICATED")
    @GetMapping(value = "/current")
    public ResponseEntity<UserDTO> currentUser(
            @ApiIgnore
            @GetUser User user
    ){
        return ResponseEntity.ok(new UserDTO(user, true));
    }

    @ApiOperation(value = "Update current user by id; access: ANY AUTHENTICATED")
    @PutMapping(value = "/current")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @ApiIgnore
            @GetUser User user,
            @RequestBody UserDTO userDTO
    ) throws UserNotFoundException, UsernameAlreadyUsedException, EmailAlreadyUsedException {
        return ResponseEntity.of(userService.update(userDTO, user.getId()));
    }


    @ApiOperation(value = "Check login is available or not; access: ANY",
        notes = "if response return username, so it is free")
    @PostMapping(value = "/check-username")
    public ResponseEntity<String> checkUsernameExist(
            @RequestBody String username
    ) throws UsernameAlreadyUsedException {

        if(userService.findOneByLogin(username).isPresent()){
           throw new UsernameAlreadyUsedException();
        }
        return ResponseEntity.ok(username);
    }

    @ApiOperation(value = "Check email is available or not; access: ANY",
            notes = "if response return email, so it is free")
    @PostMapping(value = "/check-email")
    public ResponseEntity<String> checkEmailExist(
            @RequestBody String email
    ) throws EmailAlreadyUsedException {

        if (userService.findOneByEmailIgnoreCase(email).isPresent()){
            throw new EmailAlreadyUsedException();
        }
        return ResponseEntity.ok(email);
    }

}
