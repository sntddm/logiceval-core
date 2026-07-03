package sa.logiceval.security.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    Optional<SystemUser> findByUsername(String username);
}