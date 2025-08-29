package com.docapp.repo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.docapp.entity.UserAccount;
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByToken(String token);
}
