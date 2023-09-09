package com.letschat.chat.repository;

import com.letschat.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
	
	@Query("FROM Conversation c " +
		   "WHERE c.userOne.username " +
		   "IN (:userOne, :userTwo) " +
		   "AND c.userTwo.username " +
		   "IN (:userOne, :userTwo)")
	Conversation getConversation(@Param("userOne") String userOne,
                                  @Param("userTwo") String userTwo);
	
	@Query("FROM Conversation c " +
		   "WHERE c.id = :conversationId")
	Optional<Conversation> findById(@Param("conversationId") UUID conversationId);
	
	@Query("FROM Conversation c " +
		   "WHERE c.userOne.username = :username " +
		   "OR c.userTwo.username = :username")
	List<Conversation> getAllConversations(@Param("username") String username);
	
	@Query("SELECT COUNT(c) > 0 " +
		   "FROM Conversation c " +
		   "WHERE c.userOne.username IN (:userOne, :userTwo) " +
		   "AND c.userTwo.username IN (:userOne, :userTwo)")
	Boolean existsConversation(@Param("userOne") String userOne,
	                           @Param("userTwo") String userTwo);
	
	@Modifying
	void deleteById(UUID conversationId);
}
