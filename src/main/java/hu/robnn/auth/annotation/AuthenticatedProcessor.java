package hu.robnn.auth.annotation;

import hu.robnn.auth.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Component
public class AuthenticatedProcessor {

    private static final Logger LOGGER = getLogger(AuthenticatedProcessor.class);

    private final HttpServletRequest request;
    private final UserService userService;

    @Autowired
    public AuthenticatedProcessor(HttpServletRequest request, UserService userService) {
        this.request = request;
        this.userService = userService;
    }

    @Around("@annotation(Authenticated)")
    public Object authenticate(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Authenticated authenticated = method.getAnnotation(Authenticated.class);
        LOGGER.info("Authentication requested for method {}", method.getName());
        userService.authenticate(request.getHeader("X-Auth-Token"), authenticated.acceptedRoles());
        return pjp.proceed();
    }
}
