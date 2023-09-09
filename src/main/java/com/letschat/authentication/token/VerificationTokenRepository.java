package com.letschat.authentication.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
	
	Optional<VerificationToken> findByToken(String token);
	List<VerificationToken> findAllByUser_Email(String username);
	@Modifying
	@Query("update VerificationToken t set t.verifiedAt = :verifiedAt where t.token = :token")
	void setVerifiedAt(@Param("verifiedAt") LocalDateTime time, @Param("token") String token);
}
