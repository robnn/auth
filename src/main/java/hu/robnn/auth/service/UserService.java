package hu.robnn.auth.service;

import hu.robnn.auth.dao.TokenDao;
import hu.robnn.auth.dao.UserDao;
import hu.robnn.auth.dao.model.User;
import hu.robnn.auth.dao.model.UserToken;
import hu.robnn.auth.dao.model.dto.UserDTO;
import hu.robnn.auth.enums.UserRole;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class UserService {

    private static final Logger LOGGER = getLogger(UserService.class);

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final Collection<RegisterInterceptor> registerInterceptors;
    private final Collection<LoginInterceptor> loginInterceptors;
    private final Collection<AuthenticateInterceptor> authenticateInterceptors;

    @Autowired
    public UserService(UserDao userDao, TokenDao tokenDao,
                       PasswordEncoder passwordEncoder, UserMapper userMapper, ApplicationContext context) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.registerInterceptors = context.getBeansOfType(RegisterInterceptor.class).values();
        this.loginInterceptors = context.getBeansOfType(LoginInterceptor.class).values();
        this.authenticateInterceptors = context.getBeansOfType(AuthenticateInterceptor.class).values();
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (!userDao.findByUserName(userDTO.getUsername()).isEmpty()) {
            LOGGER.info("Registration failed, used username provided: {}", userDTO.getUsername());
            throw new UserException(UserError.USED_USERNAME);
        } else if (!userDao.findByEmailAddress(userDTO.getEmailAddress()).isEmpty()) {
            LOGGER.info("Registration failed, used email address provided: {}", userDTO.getEmailAddress());
            throw new UserException(UserError.USED_EMAIL_ADDRESS);
        } else {
            User user = new User();
            user.setRealName(userDTO.getRealName());
            user.setEmailAddress(userDTO.getEmailAddress());
            user.setRole(UserRole.USER.name());
            user.setUsername(userDTO.getUsername());
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
            LOGGER.info("Calling executeBeforeRegistration method on registered registerInterceptors: {}", registerInterceptors);
            registerInterceptors.forEach(registerInterceptor -> registerInterceptor.executeBeforeRegistration(user));
            User saved = userDao.save(user);
            LOGGER.info("Registration was successful, new user: {}", saved.getUsername());
            LOGGER.info("Calling executeAfterRegistration method on registered registerInterceptors: {}", registerInterceptors);
            registerInterceptors.forEach(registerInterceptor -> registerInterceptor.executeAfterRegistration(saved));
            return userMapper.map(saved);
        }
    }

    public String login(UserDTO userDTO) {
        List<User> users = userDao.findByUserName(userDTO.getUsername());
        if (users.isEmpty()) {
            LOGGER.info("Login failed, invalid username provided: {}", userDTO.getUsername());
            throw new UserException(UserError.INVALID_CREDENTIALS);
        }
        User user = users.get(0);
        LOGGER.info("Calling executeBeforeLogin method on registered loginInterceptors: {}", loginInterceptors);
        loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeBeforeLogin(user));
        if (user.getPasswordHash() != null && passwordEncoder
                .matches(userDTO.getPassword(), user.getPasswordHash())) {
            List<UserToken> loggedInUserTokens = tokenDao.findByUserOrderByValidToDesc(users.get(0));
            if (!loggedInUserTokens.isEmpty() && isTokenValid(loggedInUserTokens.get(0))) {
                renewToken(loggedInUserTokens.get(0));
                LOGGER.info("Existing and valid token found for user, login was successful. User: {}", userDTO.getUsername());
                loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeAfterLogin(users.get(0)));
                return loggedInUserTokens.get(0).getToken();
            } else if (!loggedInUserTokens.isEmpty()) {
                LOGGER.info("Existing but invalid token found for user, deleting it. User: {}", userDTO.getUsername());
                tokenDao.delete(loggedInUserTokens.get(0));
            }
            UserToken userToken = createUserToken(user);
            tokenDao.save(userToken);
            LOGGER.info("Login was successful for user: {}", userDTO.getUsername());
            LOGGER.info("Calling executeAfterLogin method on registered loginInterceptors: {}", loginInterceptors);
            loginInterceptors.forEach(loginInterceptor -> loginInterceptor.executeAfterLogin(users.get(0)));
            return userToken.getToken();
        } else {
            LOGGER.info("Login failed, invalid password provided for user: {}", userDTO.getUsername());
            throw new UserException(UserError.INVALID_CREDENTIALS);
        }
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

    public void authenticate(String token, UserRole requiredRole) {
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
                UserRole userRole = UserRole.valueOf(user.getRole());
                if (userRole.getPermissionLevel() < requiredRole.getPermissionLevel()) {
                    LOGGER.info("Authentication failed, permission was insufficient. Needed: {}, presented: {}, User: {}", requiredRole, userRole, user.getUsername());
                    throw new UserException(UserError.INSUFFICIENT_PERMISSION);
                }
                LOGGER.info("Authentication successful, requesting user: {}", user.getUsername());
                LOGGER.info("Calling registered authentication interceptors: {}", authenticateInterceptors);
                authenticateInterceptors.forEach(authenticateInterceptor -> authenticateInterceptor.executeAfterAuthentication(user));
            }
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
