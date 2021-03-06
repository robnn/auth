package hu.robnn.auth.dao.model

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.robnn.commons.interfaces.UuidHolder
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "au_user_token")
open class UserToken : UuidHolder{
    override fun setUuid(p0: String?) {
        if(!p0.isNullOrEmpty()) {
            uuid = p0!!
        }
    }

    override fun getUuid(): String {
        return uuid
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_user_token_seq")
    @SequenceGenerator(name = "au_user_token_seq", sequenceName = "au_user_token_seq", allocationSize = 1)
    @Column(name = "id")
    @JsonIgnore
    var id: Long? = null

    @Column(name = "uuid")
    private var uuid: String = UUID.randomUUID().toString()

    @Column(name = "token")
    open var token: String? = null

    @Column(name = "valid_to")
    open var validTo: LocalDateTime? = null

    @ManyToOne(targetEntity = User::class)
    @JoinColumn(name = "user_id")
    open var user: User? = null



}