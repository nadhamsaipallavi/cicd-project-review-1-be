package com.propertymanagement.model;

import com.propertymanagement.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "property_inspections")
@EqualsAndHashCode(callSuper = true)
public class PropertyInspection extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;
    
    @Column(name = "inspection_date", nullable = false)
    private LocalDateTime inspectionDate;
    
    @Column(name = "inspection_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InspectionType inspectionType;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InspectionStatus status;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "issues_found")
    private String issuesFound;
    
    @Column(name = "recommendations")
    private String recommendations;
    
    @Column(name = "next_inspection_date")
    private LocalDateTime nextInspectionDate;
} 