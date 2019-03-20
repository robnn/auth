package hu.robnn.auth.mapper


import hu.robnn.auth.dao.model.Role
import hu.robnn.auth.dao.model.User
import hu.robnn.auth.dao.model.dto.RoleDTO
import hu.robnn.auth.dao.model.dto.UserDTO
import org.springframework.stereotype.Component


@Component
class UserMapper {

    fun map(source: User) : UserDTO {
        val target = UserDTO()
        target.uuid = source.uuid
        target.emailAddress = source.emailAddress
        target.realName = source.realName
        target.roles = source.roles.map { map(it) }
        target.username = source.username
        return target
    }

    fun map(source: Role) : RoleDTO {
        val target = RoleDTO()
        target.roleCode = source.roleCode
        target.uuid = source.uuid
        return target
    }
}