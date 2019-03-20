package hu.robnn.auth.dao.model.dto

import hu.robnn.commons.interfaces.UuidHolder
import java.util.*

class RoleDTO : UuidHolder {
    override fun setUuid(p0: String?) {
        if(!p0.isNullOrEmpty()) {
            uuid = UUID.fromString(p0!!)
        }
    }

    override fun getUuid(): String {
        return uuid.toString()
    }
    private var uuid: UUID = UUID.randomUUID()

    var roleCode: String? = null
}