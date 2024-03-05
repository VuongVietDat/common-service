package vn.com.atomi.loyalty.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {}
