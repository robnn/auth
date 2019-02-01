package hu.robnn.auth.service.interceptors;

import hu.robnn.auth.dao.model.User;

/**
 * Interceptor interface for executing code during authentication.
 * UserService will inject the implementors, and call them.
 * Implementors should annotated with @Component
 * @see org.springframework.stereotype.Component
 */
public interface AuthenticateInterceptor {

    /**
     * Will be called after successful authentication
     * @param user the entity from the DB will be passed
     */
    void executeAfterAuthentication(User user);
}
