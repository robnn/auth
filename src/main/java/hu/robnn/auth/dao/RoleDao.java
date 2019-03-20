package hu.robnn.auth.dao;

import hu.robnn.auth.dao.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDao extends CrudRepository<Role, Long> {
    Optional<Role> findByRoleCode(String roleCode);
}
