package hu.robnn.auth;

import hu.robnn.auth.dao.RoleDao;
import hu.robnn.auth.dao.model.Role;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfiguration {
    public final static String USER_ROLE_CODE = "USER";
    private final static String ADMIN_ROLE_CODE = "ADMIN";

    private final RoleDao roleDao;

    public AuthConfiguration(RoleDao roleDao) {
        this.roleDao = roleDao;
        createBaseRolesIfNeeded();
    }

    private void createBaseRolesIfNeeded() {
        if (!checkRoleExistence(USER_ROLE_CODE)) {
            roleDao.save(Role.Companion.buildForName(USER_ROLE_CODE));
        }
        if (!checkRoleExistence(ADMIN_ROLE_CODE)) {
            roleDao.save(Role.Companion.buildForName(ADMIN_ROLE_CODE));
        }
    }

    private boolean checkRoleExistence(String roleCode) {
        return roleDao.findByRoleCode(roleCode).isPresent();
    }
}
