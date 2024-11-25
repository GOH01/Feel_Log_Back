package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);
    List<User> findByUserName(String userName);

}
