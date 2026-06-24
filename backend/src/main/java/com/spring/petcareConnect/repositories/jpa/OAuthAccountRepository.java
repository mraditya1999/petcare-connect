package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.OAuthAccount;
import com.spring.petcareConnect.enums.AuthProvider;
import com.spring.petcareConnect.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    List<OAuthAccount> findByUser(User user);
}

