package com.propertymanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime resolvedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceRequestStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceRequestPriority priority;
    
    @Column(nullable = false)
    private String category;
    
    @ElementCollection
    @CollectionTable(name = "maintenance_request_images")
    private List<String> images = new ArrayList<>();
    
    private String assignedTo;
    
    private String contactPhone;
    
    private BigDecimal estimatedCost;
    
    private BigDecimal actualCost;
    
    private String notes;
    
    @OneToMany(mappedBy = "maintenanceRequest", cascade = CascadeType.ALL)
    private List<MaintenanceRequestComment> comments = new ArrayList<>();
    
    private String permitRequired;
    
    private String permitStatus;
    
    private String permitNumber;
    
    @Column(nullable = false)
    private boolean emergencyRequest;
    
    private String emergencyContacts;
    
    private LocalDateTime scheduledDate;
    
    private String scheduledTimeSlot;
    
    private String preferredTimeSlot;
    
    private String resolution;
    
    private String additionalNotes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = MaintenanceRequestStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Explicit getters and setters to ensure availability
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Property getProperty() {
        return property;
    }
    
    public void setProperty(Property property) {
        this.property = property;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public MaintenanceRequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(MaintenanceRequestStatus status) {
        this.status = status;
    }
    
    public MaintenanceRequestPriority getPriority() {
        return priority;
    }
    
    public void setPriority(MaintenanceRequestPriority priority) {
        this.priority = priority;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
} 