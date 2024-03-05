package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsernameAndDeletedFalse(String username);
}
