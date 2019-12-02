package hu.robnn.auth.service;

import hu.robnn.auth.AuthConfiguration;
import hu.robnn.auth.dao.RoleDao;
import hu.robnn.auth.dao.TokenDao;
import hu.robnn.auth.dao.UserDao;
import hu.robnn.auth.dao.model.Role;
import hu.robnn.auth.dao.model.User;
import hu.robnn.auth.dao.model.UserToken;
import hu.robnn.auth.dao.model.dto.UserDTO;
import hu.robnn.auth.exception.UserError;
import hu.robnn.auth.exception.UserException;
import hu.robnn.auth.mapper.UserMapper;
import hu.robnn.auth.service.interceptors.AuthenticateInterceptor;
import hu.robnn.auth.service.interceptors.LoginInterceptor;
import hu.robnn.auth.service.interceptors.RegisterInterceptor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class UserService {

    private static final Logger LOGGER = getLogger(UserService.class);

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleDao roleDao;

    private final Collection<RegisterInterceptor> registerInterceptors;
    private final Collection<LoginInterceptor> loginInterceptors;
    private final Collection<AuthenticateInterceptor> authenticateInterceptors;

    @Autowired
    public UserService(UserDao userDao, TokenDao tokenDao,
                       PasswordEncoder passwordEncoder, UserMapper userMapper, RoleDao roleDao, ApplicationContext context) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleDao = roleDao;
        this.registerInterceptors = context.getBeansOfType(RegisterInterceptor.class).values();
        this.loginInterceptors = context.getBeansOfType(LoginInterceptor.class).values();
        this.authenticateInterceptors = context.getBeansOfType(AuthenticateInterceptor.class).values();
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO, boolean withoutPassword) {
        if (userDao.findByUsername(userDTO.getUsername()).isPresent()) {
            LOGGER.info("Registration failed, used username provided: {}", userDTO.getUsername());
            throw new UserException(UserError.USED_USERNAME);
        } else if (userDao.findByEmailAddress(userDTO.getEmailAddress()).isPresent()) {
            LOGGER.info("Registration failed, used email address provided: {}", userDTO.getEmailAddress());
            throw new UserException(UserError.USED_EMAIL_ADDRESS);
        } else {
            User user = new User();
            user.setRealName(userDTO.getRealName());
            user.setEmailAddress(userDTO.getEmailAddress());
            Optional<Role> userRole = roleDao.findByRoleCode(AuthConfiguration.USER_ROLE_CODE);
            if (!userRole.isPresent()) {
                throw new IllegalStateException("User role must exist in DB!");
            }
            user.getRoles().add(userRole.get());
            user.setUsername(userDTO.getUsername());
            if (!withoutPassword) {
                user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
            }
            LOGGER.info("Calling executeBeforeRegistration method on registered registerInterceptors: {}", registerInterceptors);
            registerInterceptors.forEach(registerInterceptor -> registerInterceptor.executeBeforeRegistration(user));
            User saved = userDao.save(user);
            LOGGER.info("Registration was successful, new user: {}", saved.getUsername());
            LOGGER.info("Calling executeAfterRegistration method on registered registerInterceptors: {}", registerInterceptors);
            registerInterceptors.forEach(registerInterceptor -> registerInterceptor.executeAfterRegistration(saved));
            return userMapper.map(saved);
        }
    }

    public String loginWithoutPassword(UserDTO userDTO) {
        User user = findUserInDb(userDTO);
        LOGGER.info("Calling executeBeforeLogin method on registered loginInterceptors: {}", loginInterceptors);
        loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeBeforeLogin(user));
        return executeCommonLoginFlow(userDTO, user);
    }

    public String login(UserDTO userDTO) {
        User user = findUserInDb(userDTO);
        LOGGER.info("Calling executeBeforeLogin method on registered loginInterceptors: {}", loginInterceptors);
        loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeBeforeLogin(user));
        if (user.getPasswordHash() != null && passwordEncoder
                .matches(userDTO.getPassword(), user.getPasswordHash())) {
            return executeCommonLoginFlow(userDTO, user);
        } else {
            LOGGER.info("Login failed, invalid password provided for user: {}", userDTO.getUsername());
            throw new UserException(UserError.INVALID_CREDENTIALS);
        }
    }

    private User findUserInDb(UserDTO userDTO) {
        Optional<User> userOptional = userDao.findByUsername(userDTO.getUsername());
        if (!userOptional.isPresent()) {
            LOGGER.info("Login failed, invalid username provided: {}", userDTO.getUsername());
            throw new UserException(UserError.INVALID_CREDENTIALS);
        }
        return userOptional.get();
    }

    private String executeCommonLoginFlow(UserDTO userDTO, User user) {
        List<UserToken> loggedInUserTokens = tokenDao.findByUserOrderByValidToDesc(user);
        if (!loggedInUserTokens.isEmpty() && isTokenValid(loggedInUserTokens.get(0))) {
            renewToken(loggedInUserTokens.get(0));
            LOGGER.info("Existing and valid token found for user, login was successful. User: {}", userDTO.getUsername());
            loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeAfterLogin(user));
            return loggedInUserTokens.get(0).getToken();
        } else if (!loggedInUserTokens.isEmpty()) {
            LOGGER.info("Existing but invalid token found for user, deleting it. User: {}", userDTO.getUsername());
            tokenDao.delete(loggedInUserTokens.get(0));
        }
        UserToken userToken = createUserToken(user);
        tokenDao.save(userToken);
        LOGGER.info("Login was successful for user: {}", userDTO.getUsername());
        LOGGER.info("Calling executeAfterLogin method on registered loginInterceptors: {}", loginInterceptors);
        loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeAfterLogin(user));
        UserContext.Companion.setCurrentUser(user);
        return userToken.getToken();
    }

    private UserToken createUserToken(User user) {
        UserToken userToken = new UserToken();
        userToken.setToken(RandomStringUtils.randomAlphabetic(32));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        LocalDateTime localDate = LocalDateTime
                .ofInstant(calendar.toInstant(), ZoneId.systemDefault());
        userToken.setValidTo(localDate);
        userToken.setUser(user);
        return userToken;
    }

    public void authenticate(String token, String[] acceptedRoles) {
        UserToken userToken = tokenDao.findByToken(token);
        if (userToken == null || (userToken.getValidTo() != null && !isTokenValid(userToken))) {
            LOGGER.info("Authentication failed, invalid token provided");
            if(userToken != null) {
                LOGGER.info("Token was invalid because of timeout, deleting token");
                tokenDao.delete(userToken);
            }
            throw new UserException(UserError.INVALID_TOKEN);
        } else {
            LOGGER.info("Token is valid, renewing token");
            renewToken(userToken);
            User user = userToken.getUser();
            if (user != null) {
                Set<String> userRoles = user.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toSet());
                if (Collections.disjoint(userRoles, Arrays.asList(acceptedRoles))) {
                    LOGGER.info("Authentication failed, permission was insufficient. Accepted roles: {}, presented: {}, User: {}", acceptedRoles, userRoles, user.getUsername());
                    throw new UserException(UserError.INSUFFICIENT_PERMISSION);
                }
                LOGGER.info("Authentication successful, requesting user: {}", user.getUsername());
                LOGGER.info("Calling registered authentication interceptors: {}", authenticateInterceptors);
                authenticateInterceptors.forEach(authenticateInterceptor -> authenticateInterceptor.executeAfterAuthentication(user));
                UserContext.Companion.setCurrentUser(user);
            }
        }
    }

    @Transactional
    public UserDTO addRolesToUser(String userName, Set<String> newRoleCodes) {
        Optional<User> byUsername = userDao.findByUsername(userName);
        if (byUsername.isPresent()){
            User user = byUsername.get();
            Set<Role> newRoles = newRoleCodes.stream().map(Role.Companion::buildForName).collect(Collectors.toSet());
            user.getRoles().addAll(newRoles);
            userDao.save(user);
            return userMapper.map(user);
        } else {
            throw new UserException(UserError.NO_USER_FOR_USERNAME);
        }

    }

    private void renewToken(UserToken userToken) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        userToken
                .setValidTo(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()));
        tokenDao.save(userToken);
    }

    private boolean isTokenValid(UserToken userToken) {
        if (userToken != null && userToken.getValidTo() != null) {
            return LocalDateTime.now().isBefore(userToken.getValidTo());
        }
        return false;
    }

    public Optional<UserDTO> getUserForToken(String token) {
        if (token != null) {
            UserToken tokenDMO = tokenDao.findByToken(token);
            if (tokenDMO != null) {
                User source = tokenDMO.getUser();
                if (source != null) {
                    return Optional.of(userMapper.map(source));
                }
            }
        }
        return Optional.empty();
    }
}
