package hu.robnn.auth.annotation;

import hu.robnn.auth.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation class to use on API/service methods, where the authentication is needed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authenticated {
    /**
     * Used to specify the needed UserRole, for the authentication
     */
    UserRole neededRole() default UserRole.USER;
}
