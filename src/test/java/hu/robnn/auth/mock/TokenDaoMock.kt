package hu.robnn.auth.mock

import hu.robnn.auth.dao.TokenDao
import hu.robnn.auth.dao.model.User
import hu.robnn.auth.dao.model.UserToken
import java.util.*
import kotlin.collections.HashMap

class TokenDaoMock: TokenDao {
    private var counter = 0
    private val map = HashMap<Long, UserToken?>()


    override fun <S : UserToken?> save(entity: S): S {
        if(map.containsKey(entity?.id)) {
            map[entity?.id!!] = entity
        } else {
            entity?.id = counter.toLong()
            map[counter.toLong()] = entity
            counter++
        }
        return entity
    }

    override fun findAll(): MutableCollection<UserToken?> = map.values

    override fun deleteById(id: Long) {
        map.remove(id)
    }

    override fun findByUserOrderByValidToDesc(user: User?): MutableList<UserToken?> {
        return map.values.filter { it?.user == user }.sortedByDescending { it?.validTo }.toMutableList()
    }

    override fun deleteAll(entities: MutableIterable<UserToken>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        map.clear()
    }

    override fun <S : UserToken?> saveAll(entities: MutableIterable<S>): List<S> {
        return entities.map { save(it) }
    }

    override fun count(): Long = counter.toLong()

    override fun findByToken(token: String?): UserToken? {
        return map.values.firstOrNull { it?.token == token }
    }

    override fun findAllById(ids: MutableIterable<Long>): MutableIterable<UserToken> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: Long): Boolean = id < counter

    override fun findById(id: Long): Optional<UserToken> {
        return Optional.ofNullable(map[id])
    }

    override fun delete(entity: UserToken) {
        map.remove(entity.id)
    }

    fun reset() {
        counter = 0
        map.clear()
    }
}