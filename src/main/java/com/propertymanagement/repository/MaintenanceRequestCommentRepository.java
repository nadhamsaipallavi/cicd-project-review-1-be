package com.propertymanagement.repository;

import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.MaintenanceRequestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestCommentRepository extends JpaRepository<MaintenanceRequestComment, Long> {
    
    List<MaintenanceRequestComment> findByMaintenanceRequestIdOrderByCreatedAtAsc(Long maintenanceRequestId);
    
    void deleteByMaintenanceRequestId(Long maintenanceRequestId);
} 