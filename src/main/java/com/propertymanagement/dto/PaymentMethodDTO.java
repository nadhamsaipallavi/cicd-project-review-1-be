package com.propertymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long id;
    private String type;
    private String last4;
    private Integer expiryMonth;
    private Integer expiryYear;
    private boolean isDefault;
    private Long userId;
} 