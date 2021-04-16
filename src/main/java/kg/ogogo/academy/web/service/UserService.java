package kg.ogogo.academy.web.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import kg.ogogo.academy.web.domain.security.QUser;
import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.domain.security.UserGroup;
import kg.ogogo.academy.web.dto.NewUserDTO;
import kg.ogogo.academy.web.dto.UserDTO;
import kg.ogogo.academy.web.repository.UserGroupRepository;
import kg.ogogo.academy.web.repository.UserRepository;
import kg.ogogo.academy.web.service.exception.EmailAlreadyUsedException;
import kg.ogogo.academy.web.service.exception.InvalidCredentialException;
import kg.ogogo.academy.web.service.exception.UserNotFoundException;
import kg.ogogo.academy.web.service.exception.UsernameAlreadyUsedException;
import kg.ogogo.academy.web.service.impl.UserServiceImpl;
import kg.ogogo.academy.web.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserGroupRepository userGroupRepository;

    private final UserServiceImpl userServiceImpl;

    private final PasswordEncoder passwordEncoder;

    private final CacheManager cacheManager;

    public static String ROLE_ADMIN = "ROLE_ADMIN";

    public static String ROLE_OWNER = "ROLE_OWNER";


    public UserService(UserRepository userRepository, UserGroupRepository userGroupRepository, UserServiceImpl userServiceImpl, PasswordEncoder passwordEncoder, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.cacheManager = cacheManager;
    }

    public Page<UserDTO> getAllUsers(@NotNull Predicate predicate, @NotNull Pageable pageable,
                                     User user
    ) {
        final QUser qUser = QUser.user;
        final BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(qUser.isNotNull());
        return userRepository.findAll(builder.getValue(), pageable).map(
                us -> new UserDTO(us, true));
    }

    public User findByLogin(String username) throws UserNotFoundException {
        return userRepository.findOneByLogin(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }


    public User addUser(NewUserDTO userDTO, User current) throws EmailAlreadyUsedException, UsernameAlreadyUsedException {
        if (findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new UsernameAlreadyUsedException();
        } else if (findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User user = new User();
            user.setLogin(userDTO.getLogin().toLowerCase());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            if (userDTO.getEmail() != null) {
                user.setEmail(userDTO.getEmail().toLowerCase());
            }

            String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
            user.setPassword(encryptedPassword);
            user.setResetKey(RandomUtil.generateRandomAlphaNum());
            user.setResetDate(Instant.now());
            user.setCreatedAt(LocalDateTime.now());
            user.setActivated(true);
            if (userDTO.getUserGroups() != null) {
                Set<UserGroup> userGroups = userDTO.getUserGroups().stream()
                        .map(userGroupRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                user.setUserGroups(userGroups);
            }
            user = userRepository.save(user);
            this.clearUserCaches(user);
            log.debug("Created Information for User: {}", user);
            return user;
        }
    }


    public Optional<UserDTO> update(UserDTO userDTO, Long id) throws UserNotFoundException, EmailAlreadyUsedException, UsernameAlreadyUsedException {

        Optional<User> user = userRepository.findById(id

        );
        Optional<User> existingUser;
        if (user.isPresent() && (!user.get().getEmail().equals(userDTO.getEmail()))) {
            existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
            if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
                throw new EmailAlreadyUsedException();
            }
        }
        if (user.isPresent() && userDTO.getLogin() != null && (!user.get().getLogin().equals(userDTO.getLogin()))) {

            existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
            if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
                throw new UsernameAlreadyUsedException();
            }
        }

        return userServiceImpl.updateUser(userDTO, user.get());

    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    this.clearUserCaches(user);
                    return user;
                });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
                .filter(User::isActivated)
                .map(user -> {
                    user.setResetKey(RandomUtil.generateRandomAlphaNum());
                    user.setResetDate(Instant.now());
                    this.clearUserCaches(user);
                    return user;
                });
    }


    public User getUser(org.springframework.security.core.userdetails.User user) {
        return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(user.getUsername()).orElseThrow(
                InvalidCredentialException::new
        );
    }

    public Boolean isUserGroupContains(User user, String code) {
        Set<String> gp = user.getUserGroups().stream().map(UserGroup::getCode).collect(Collectors.toSet());
        return gp.contains(code);
    }

    public Boolean isAdmin(User user){
        return isUserGroupContains(user, ROLE_ADMIN);
    }

    public Optional<User> findOneByLogin(String username) {
        return userRepository.findOneByLogin(username);
    }

    public Optional<User> findOneByEmailIgnoreCase(String email) {
        return userRepository.findOneByEmailIgnoreCase(email);
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

    public User save(User user){
        this.clearUserCaches(user);
        return userRepository.save(user);
    }


}
