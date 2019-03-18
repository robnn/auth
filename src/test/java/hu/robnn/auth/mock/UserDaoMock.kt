package hu.robnn.auth.mock

import hu.robnn.auth.dao.UserDao
import hu.robnn.auth.dao.model.User
import java.util.*

class UserDaoMock: UserDao {

    private var counter = 0
    private val map = HashMap<Long, User?>()

    override fun <S : User?> save(entity: S): S {
        entity?.id = counter.toLong()
        map[counter.toLong()] = entity
        counter ++
        return entity
    }

    override fun findByUsername(name: String?): List<User?> {
        return map.values.filter { it?.username == name }
    }

    override fun findAll(): MutableCollection<User?> = map.values

    override fun deleteById(id: Long) {
        map.remove(id)
    }

    override fun deleteAll(entities: MutableIterable<User>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        map.clear()
    }

    override fun <S : User?> saveAll(entities: MutableIterable<S>): List<S> {
        return entities.map { save(it) }
    }

    override fun count(): Long = counter.toLong()

    override fun findAllById(ids: MutableIterable<Long>): MutableIterable<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: Long): Boolean = id < counter

    override fun findById(id: Long): Optional<User> {
        return Optional.ofNullable(map[id])
    }

    override fun findByEmailAddress(email: String?): List<User?> {
        return map.values.filter { it?.emailAddress == email }
    }

    override fun delete(entity: User) {
        map.remove(entity.id)
    }

    fun reset() {
        counter = 0
        map.clear()
    }

}