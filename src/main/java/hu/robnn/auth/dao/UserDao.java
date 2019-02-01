package hu.robnn.auth.dao;

import hu.robnn.auth.dao.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByUuid(String uuid);
    List<User> findByUserName(String name);
    List<User> findByEmailAddress(String email);
}
