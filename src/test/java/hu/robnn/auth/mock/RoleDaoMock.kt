package hu.robnn.auth.mock

import hu.robnn.auth.dao.RoleDao
import hu.robnn.auth.dao.model.Role
import java.util.*

class RoleDaoMock : RoleDao {
    override fun <S : Role?> save(entity: S): S {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): MutableIterable<Role> {
        return mutableSetOf(Role.buildForName("USER"), Role.buildForName("ADMIN"))
    }

    override fun findByRoleCode(roleCode: String?): Optional<Role> {
        if (roleCode == "USER")
            return Optional.of(Role.buildForName("USER"))
        if (roleCode == "ADMIN")
            return Optional.of(Role.buildForName("ADMIN"))
        return Optional.empty()
    }

    override fun deleteById(id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(entities: MutableIterable<Role>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : Role?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun count(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllById(ids: MutableIterable<Long>): MutableIterable<Role> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: Long): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: Long): Optional<Role> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(entity: Role) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}