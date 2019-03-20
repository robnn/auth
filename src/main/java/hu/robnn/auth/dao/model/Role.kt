package hu.robnn.auth.dao.model

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.robnn.commons.interfaces.UuidHolder
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "au_role")
class Role : UuidHolder {

    companion object {
        fun buildForName(name: String?) : Role = Role().apply { roleCode = name }
    }

    override fun setUuid(p0: String?) {
        if (!p0.isNullOrEmpty()) {
            uuid = p0!!
        }
    }

    override fun getUuid(): String {
        return uuid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        if (roleCode != other.roleCode) return false

        return true
    }

    override fun hashCode(): Int {
        return roleCode?.hashCode() ?: 0
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_user_role_seq")
    @SequenceGenerator(name = "au_user_role_seq", sequenceName = "au_user_role_seq", allocationSize = 1)
    @Column(name = "id")
    @JsonIgnore
    var id: Long? = null

    @Column(name = "uuid")
    private var uuid: String = UUID.randomUUID().toString()

    @Column(name = "role_code")
    var roleCode: String? = null

    @ManyToMany(mappedBy = "roles")
    var users: List<User> = mutableListOf()


}