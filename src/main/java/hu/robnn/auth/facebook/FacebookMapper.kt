package hu.robnn.auth.facebook

import hu.robnn.auth.dao.model.dto.UserDTO
import org.springframework.stereotype.Component

@Component
class FacebookMapper {
    fun mapToUserDTO(facebookUser: FacebookUser) : UserDTO {
        val target = UserDTO()
        target.realName = facebookUser.name
        target.username = generateUserName(facebookUser)
        target.emailAddress = facebookUser.email
        return target
    }

    private fun generateUserName(facebookUser: FacebookUser): String =
            facebookUser.email!!.split("@")[0] + facebookUser.id
}