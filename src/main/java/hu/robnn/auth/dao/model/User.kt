package hu.robnn.auth.dao.model

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.robnn.commons.interfaces.UuidHolder
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "au_user")
open class User : UuidHolder{
    override fun setUuid(p0: String?) {
        if(!p0.isNullOrEmpty()) {
            uuid = p0!!
        }
    }

    override fun getUuid(): String {
        return uuid
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_user_seq")
    @SequenceGenerator(name = "au_user_seq", sequenceName = "au_user_seq", allocationSize = 1)
    @Column(name = "id")
    @JsonIgnore
    var id: Long? = null

    @Column(name = "uuid")
    private var uuid: String = UUID.randomUUID().toString()

    @Column(name = "real_name")
    var realName: String? = null

    @Column(name = "user_name")
    var userName: String? = null

    @Column(name = "email_address")
    var emailAddress: String? = null

    @Column(name = "password_hash")
    var passwordHash: String? = null

    @Column(name = "role")
    var role: String? = null

}