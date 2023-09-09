package com.letschat.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByEmailIgnoreCase(String email);
	
	@Query("select case when count(u) > 0 then true else false end from User u where u.accountLocked and u.email = " +
		":email")
	boolean existsUserByEmailWhereAccountLocked(@Param("email") String email);
	boolean existsByEmailIgnoreCase(String email);
	
	@Modifying
	@Query("update User u set u.password = :password where u.username = :username")
	void updatePassword(@Param("username") String username,
	                    @Param("password") String password);
	
	@Modifying
	@Query("update User u set u.email = :email where u.username = :username")
	void updateEmail(@Param("email") String email, @Param("username") String username);
	
	@Modifying
	@Query("UPDATE User u " +
		   "SET u.profilePicture = :profilePicture" +
		   " WHERE u.username = :username")
	void updateProfilePicture(@Param("profilePicture") byte[] profilePicture,
	                          @Param("username") String username);
	
	@Query("SELECT u.profilePicture " +
		   "FROM User u " +
		   "WHERE u.username = :username")
	byte[] getProfilePicture(@Param("username") String username);
	
	@Query("select instant from User u where u.username = :username")
	Optional<Instant> getLastSeen(@Param("username") String username);
	
	@Query("FROM User u " +
		   "WHERE u.lastOnline " +
		   "IS NULL")
	List<User> getOnlineUsersExceptThis();
	
	@Modifying
	@Query("UPDATE User u " +
		   "SET u.lastOnline = :lastOnline, u.sessions = u.sessions + :counter " +
		   "WHERE u.username = :username")
	void setOnline(@Param("username") String username,
	               @Param("lastOnline") Instant lastOnline,
	               @Param("counter") byte counter);
	
	@Query("SELECT u.sessions " +
		   "FROM User u " +
		   "WHERE u.username = :username")
	Byte getSessions(String username);
}
