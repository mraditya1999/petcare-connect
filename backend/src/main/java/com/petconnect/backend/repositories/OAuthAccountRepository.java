package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.OAuthAccount;
import com.petconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndProviderUserId(OAuthAccount.AuthProvider provider, String providerUserId);
    List<OAuthAccount> findByUser(User user);
}
