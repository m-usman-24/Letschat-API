package com.letschat.chat.repository;

import com.letschat.chat.entity.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	
	@Query("""
    FROM ChatMessage m
    WHERE (m.from.username = :userOneUsername OR m.to.username = :userOneUsername)
    AND (m.from.username = :userTwoUsername OR m.to.username = :userTwoUsername)
    ORDER BY m.timeSent DESC
    """)
	List<ChatMessage> findByConversation(@Param("userOneUsername") String userOneUsername,
	                                     @Param("userTwoUsername") String userTwoUsername,
	                                     PageRequest pageRequest);
	
	@Modifying
	@Query("""
	DELETE FROM ChatMessage m
	WHERE (m.from.username = :userOneUsername OR m.to.username = :userOneUsername)
	AND (m.from.username = :userTwoUsername OR m.to.username = :userTwoUsername)
	""")
	int deleteAllByUsernames(@Param("userOneUsername") String userOneUsername,
	                          @Param("userTwoUsername") String userTwoUsername);
	
	@Query("""
    FROM ChatMessage c
    WHERE c.id = :messageId
    """)
	Optional<ChatMessage> findById(@Param("messageId") UUID messageId);
	
}
