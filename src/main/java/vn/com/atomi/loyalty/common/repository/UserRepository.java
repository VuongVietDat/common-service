package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameAndDeletedFalse(String username);

  Optional<User> findByIdAndDeletedFalse(Long id);
}
