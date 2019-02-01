package hu.robnn.auth.service.interceptors;

import hu.robnn.auth.dao.model.User;

/**
 * Interceptor interface for executing code during registration.
 * Contains default empty methods, to give the opportunity to use one interceptor in one phase only
 * UserService will inject the implementors, and call them.
 * Implementors should annotated with @Component
 * @see org.springframework.stereotype.Component
 */
public interface RegisterInterceptor {

    /**
     * Will be called after saving the registered user into the DB
     * @param user the saved entity will be passed
     */
    default void executeAfterRegistration(User user){}

    /**
     * Will be called before saving the registered user into the DB
     * Can be used for user data validation, etc
     * @param user incoming user entity
     */
    @SuppressWarnings("unused")
    default void executeBeforeRegistration(User user){}
}
