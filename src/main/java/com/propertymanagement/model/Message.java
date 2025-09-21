package com.propertymanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime sentAt;
    
    private LocalDateTime readAt;
    
    @Column(nullable = false)
    private boolean isRead = false;
    
    @ElementCollection
    @CollectionTable(name = "message_attachments")
    private List<String> attachments;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;
    
    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL)
    private List<Message> replies;
    
    @Column(nullable = false)
    private boolean isArchived = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;
    
    @Column(nullable = false)
    private String messageType;
    
    @Column(nullable = false)
    private boolean isSystemMessage = false;
    
    private String systemMessageType;
    
    private LocalDateTime expireAt;
    
    @Column(nullable = false)
    private boolean requiresAction = false;
    
    private String actionType;
    
    private LocalDateTime actionCompletedAt;
    
    @Column(nullable = false)
    private boolean isActionCompleted = false;
    
    private String actionCompletedBy;
    
    private String additionalData;
} 