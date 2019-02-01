package hu.robnn.auth.enums;

/**
 * Enum class for holding user role types.
 * Currently only 2 types supported.
 */
public enum UserRole {
    ADMIN(2),
    USER(1),
    ;

    Integer permissionLevel;

    UserRole(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public Integer getPermissionLevel() {
        return permissionLevel;
    }
}
