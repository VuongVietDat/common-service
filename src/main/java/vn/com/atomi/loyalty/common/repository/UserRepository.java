package vn.com.atomi.loyalty.common.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameAndDeletedFalse(String username);

  @Query(
      """
              select u,r,p from User u
              join UserRole ur on u.id = ur.userId
              join Role r on ur.roleId = r.id
              join RolePermission rp on r.id = rp.roleId
              join Permission p on rp.permissionId = p.id
              where u.username = ?1 and u.deleted = false""")
  List<Object[]> findUserInfo(String username);
}
