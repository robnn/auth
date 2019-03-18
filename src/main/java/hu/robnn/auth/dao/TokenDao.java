package hu.robnn.auth.dao;

import hu.robnn.auth.dao.model.User;
import hu.robnn.auth.dao.model.UserToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenDao extends CrudRepository<UserToken, Long> {
    List<UserToken> findByUserOrderByValidToDesc(User user);
    UserToken findByToken(String token);
}
