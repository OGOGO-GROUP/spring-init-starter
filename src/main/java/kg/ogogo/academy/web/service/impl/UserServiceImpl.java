package kg.ogogo.academy.web.service.impl;

import kg.ogogo.academy.web.domain.security.User;
import kg.ogogo.academy.web.dto.UserDTO;
import kg.ogogo.academy.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final CacheManager cacheManager;

    private final RedisJwtService redisJwtService;

    public UserServiceImpl(UserRepository userRepository, CacheManager cacheManager, RedisJwtService redisJwtService) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.redisJwtService = redisJwtService;
    }



    @Transactional
    public Optional<UserDTO> updateUser(UserDTO userDTO, User existingUser) {
        return Optional.of(existingUser)
                .map(user -> {
                    this.clearUserCaches(user);
                    Optional.ofNullable(userDTO.getLogin()).ifPresent( e ->
                            {
                                    redisJwtService.deleteToken(user.getLogin());
                                    user.setLogin(e);
                            }
                    );
                    Optional.ofNullable(userDTO.getFirstName()).ifPresent(user::setFirstName);
                    Optional.ofNullable(userDTO.getLastName()).ifPresent(user::setLastName);
                    Optional.ofNullable(userDTO.getEmail()).ifPresent(user::setEmail);
                   /* Set<UserGroup> managedAuthorities = user.getUserGroups();

                    Optional.ofNullable(userDTO.getAuthorities()).ifPresent(
                            roles ->{
                                managedAuthorities.clear();
                                roles.stream()
                                        .map(userGroupRepository::findById)
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .forEach(managedAuthorities::add);
                            }
                    );*/
                    this.clearUserCaches(user);
                    log.debug("Changed Information for User: {}", user);
                    return user;
                })
                .map(UserDTO::new);

    }


    public void deleteCacheAndJwt(User user){
        this.clearUserCaches(user);
        redisJwtService.deleteToken(user.getLogin());
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
                .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedAtBefore(Instant.now().minus(3, ChronoUnit.DAYS))
                .forEach(user -> {
                    log.debug("Deleting not activated user {}", user.getLogin());
                    userRepository.delete(user);
                    this.clearUserCaches(user);
                });
    }



    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }


}
