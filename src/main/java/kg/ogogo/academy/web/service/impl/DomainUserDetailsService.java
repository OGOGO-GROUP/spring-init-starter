package kg.ogogo.academy.web.service.impl;

import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.domain.security.UserGroup;
import kg.ogogo.academy.web.repository.UserRepository;
import kg.ogogo.academy.web.security.TokenProvider;
import kg.ogogo.academy.web.dto.UserDTO;
import kg.ogogo.academy.web.service.exception.InvalidCredentialException;
import kg.ogogo.academy.web.service.exception.UserNotActivatedException;
import kg.ogogo.academy.web.resource.vm.LoginVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final RedisJwtService redisJwtService;


    public DomainUserDetailsService(UserRepository userRepository, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, RedisJwtService redisJwtService) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.redisJwtService = redisJwtService;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authentication user {}", email);
        Optional<User> user = userRepository.findOneByEmailIgnoreCase(email);

        user.map(us ->
                {
                    us.setSAuthorities(
                            us.getUserGroups().stream().map(UserGroup::getCode).map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet()));

                    if (!us.isActivated()){
                        throw new UserNotActivatedException(
                                String.format("User %s was not activated", us.getLogin()));
                    }
                    return us;
                }
        );

        return user.orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User %s was not found.", email)
                ));
    }
    
    private org.springframework.security.core.userdetails.User createSpringUser(String login, User user){
        if (!user.isActivated()){
            throw new UserNotActivatedException(String.format("User %s was not activated", login));
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                grantedAuthorities);

    }

    public JWTToken authenticate(LoginVM loginVM){

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getEmail(), loginVM.getPassword());
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JWTToken token = redisJwtService.getTokenByUsername(loginVM.getEmail());
            if (token!=null &&
                    tokenProvider.validateToken(token.getAccessToken())){
                return token;
            }

            boolean rememberMe = loginVM.getRememberMe() != null && loginVM.getRememberMe();
            JWTToken jwt = new JWTToken(tokenProvider.createToken(authentication, rememberMe),
                    userRepository.findOneByEmailIgnoreCase(loginVM.getEmail()).map(UserDTO::new).get()
                    );
            redisJwtService.putToken(loginVM.getEmail(), jwt);
            return jwt;

        }
        catch (BadCredentialsException e){
            log.debug("Invalid credentials of user {}", loginVM.getEmail());
            throw new InvalidCredentialException();
        }
    }
}
