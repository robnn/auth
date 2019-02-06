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
    CUSTOM_ERROR,
}

class UserException(val errorCause: UserError? = null) : RuntimeException() {
    var errorCauseString: String? = null

    constructor(errorCauseString: String): this(UserError.CUSTOM_ERROR){
        this.errorCauseString = errorCauseString
    }
}
