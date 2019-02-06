package hu.robnn.auth.mapper


import hu.robnn.auth.dao.model.User
import hu.robnn.auth.dao.model.dto.UserDTO
import org.springframework.stereotype.Component


@Component
class UserMapper {

    fun map(source: User) : UserDTO {
        val target = UserDTO()
        target.uuid = source.uuid
        target.emailAddress = source.emailAddress
        target.realName = source.realName
        target.role = source.role
        target.username = source.username
        return target
    }
}