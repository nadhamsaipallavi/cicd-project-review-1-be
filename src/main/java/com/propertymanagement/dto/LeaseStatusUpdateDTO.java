package com.propertymanagement.dto;

import com.propertymanagement.model.LeaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaseStatusUpdateDTO {
    private LeaseStatus status;
    
    // Explicit getter and setter to ensure availability
    public LeaseStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaseStatus status) {
        this.status = status;
    }
} 