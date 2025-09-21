package com.propertymanagement.repository;

import com.propertymanagement.model.Message;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findBySender(User sender);
    
    List<Message> findByReceiver(User receiver);
    
    Page<Message> findBySender(User sender, Pageable pageable);
    
    Page<Message> findByReceiver(User receiver, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.sentAt DESC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.sentAt DESC")
    Page<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);
    
    @Query("SELECT DISTINCT " +
           "CASE WHEN m.sender = :user THEN m.receiver ELSE m.sender END " +
           "FROM Message m " +
           "WHERE m.sender = :user OR m.receiver = :user")
    List<User> findConversationPartners(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    List<Message> findUnreadByReceiver(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    Long countUnreadByReceiver(@Param("user") User user);
    
    List<Message> findByProperty(Property property);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user OR m.receiver = :user) AND m.property = :property ORDER BY m.sentAt DESC")
    List<Message> findByUserAndProperty(@Param("user") User user, @Param("property") Property property);
    
    List<Message> findByIsSystemMessageTrue();
    
    List<Message> findByRequiresActionTrueAndIsActionCompletedFalse();
    
    List<Message> findBySentAtBetween(LocalDateTime startDate, LocalDateTime endDate);
} 