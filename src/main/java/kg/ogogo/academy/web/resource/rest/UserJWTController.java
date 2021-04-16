package kg.ogogo.academy.web.resource.rest;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.security.JWTFilter;
import kg.ogogo.academy.web.service.annotation.GetUser;
import kg.ogogo.academy.web.service.impl.DomainUserDetailsService;
import kg.ogogo.academy.web.service.impl.JWTToken;
import kg.ogogo.academy.web.service.impl.UserServiceImpl;
import kg.ogogo.academy.web.resource.vm.LoginVM;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "auth resource:", description = " authentication with email and password, and logout.")
public class UserJWTController {


    private final DomainUserDetailsService domainUserDetailsService;

    private final UserServiceImpl userService;

    public UserJWTController(DomainUserDetailsService domainUserDetailsService, UserServiceImpl userService) {
        this.domainUserDetailsService = domainUserDetailsService;
        this.userService = userService;
    }

    @ApiOperation(value = "Authentication with email and password; access: ANY")
    @PostMapping("/login")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {

        JWTToken jwt = domainUserDetailsService.authenticate(loginVM);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt.getAccessToken());

        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "Authentication with email and password; access: ANY AUTHENTICATED")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(
            @ApiIgnore
            @GetUser User user) {
        userService.deleteCacheAndJwt(user);
        return ResponseEntity.ok("Success logout");
    }





}
