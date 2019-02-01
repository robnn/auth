package hu.robnn.auth.service.interceptors;

import hu.robnn.auth.dao.model.User;

/**
 * Interceptor interface for executing code during login.
 * Contains default empty methods, to give the opportunity to use one interceptor in one phase only
 * UserService will inject the implementors, and call them.
 * Implementors should annotated with @Component
 * @see org.springframework.stereotype.Component
 */
public interface LoginInterceptor {

    /**
     * Will be called after successful login
     * @param user the entity from the DB will be passed
     */
    default void executeAfterLogin(User user){}

    /**
     * Will be called before login
     * Can only be called, if the user supplied correct userName, to get the entity
     * @param user the entity from the DB will be passed
     */
    default void executeBeforeLogin(User user){}
}
