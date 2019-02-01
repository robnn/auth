package hu.robnn.auth.exception

/**
 * Enum for user error codes.
 */
enum class UserError{
    INVALID_TOKEN,
    USED_USERNAME,
    USED_EMAIL_ADDRESS,
    INVALID_CREDENTIALS,
    INSUFFICIENT_PERMISSION,
}

class UserException(val errorCause: UserError) : RuntimeException()
