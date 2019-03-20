package hu.robnn.auth.dao;

import hu.robnn.auth.dao.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String name);
    Optional<User> findByEmailAddress(String email);
}
