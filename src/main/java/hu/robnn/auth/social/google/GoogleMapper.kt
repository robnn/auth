package hu.robnn.auth.social.google

import hu.robnn.auth.dao.model.dto.UserDTO
import org.springframework.stereotype.Component

@Component
class GoogleMapper {
    fun mapToUserDTO(googleUser: GoogleUser): UserDTO {
        val target = UserDTO()
        target.emailAddress = googleUser.email
        target.realName = googleUser.name
        target.username = generateUserName(googleUser)
        return target
    }

    private fun generateUserName(googleUser: GoogleUser): String =
            googleUser.email!!.split("@")[0] + googleUser.id
}