package com.propertymanagement.mapper;

import com.propertymanagement.dto.PaymentDTO;
import com.propertymanagement.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LeaseMapper leaseMapper;

    @Autowired
    private PropertyMapper propertyMapper;

    /**
     * Convert a Payment entity to PaymentDTO
     */
    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setDescription(payment.getDescription());
        
        if (payment.getLease() != null) {
            dto.setLeaseId(payment.getLease().getId());
            dto.setLease(leaseMapper.toDTO(payment.getLease()));
        }
        
        if (payment.getTenant() != null) {
            dto.setTenant(userMapper.toDTO(payment.getTenant()));
        }
        
        if (payment.getProperty() != null) {
            dto.setPropertyId(payment.getProperty().getId());
            dto.setProperty(propertyMapper.toDTO(payment.getProperty()));
        }
        
        dto.setTransactionId(payment.getTransactionId());
        
        if (payment.getPaymentDate() != null) {
            dto.setPaymentDate(payment.getPaymentDate().atStartOfDay());
        }
        
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setReceiptUrl(payment.getReceiptUrl());
        dto.setCreatedAt(payment.getCreatedAt());
        
        return dto;
    }

    /**
     * Convert a list of Payment entities to a list of PaymentDTOs
     */
    public List<PaymentDTO> toDTOList(List<Payment> payments) {
        if (payments == null) {
            return null;
        }

        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a PaymentDTO to Payment entity
     */
    public Payment toEntity(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setId(paymentDTO.getId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setDescription(paymentDTO.getDescription());
        payment.setTransactionId(paymentDTO.getTransactionId());
        
        if (paymentDTO.getPaymentDate() != null) {
            payment.setPaymentDate(paymentDTO.getPaymentDate().toLocalDate());
        }
        
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setStatus(paymentDTO.getStatus());
        payment.setReceiptUrl(paymentDTO.getReceiptUrl());
        payment.setCreatedAt(paymentDTO.getCreatedAt());
        
        // Note: The lease, tenant, and property relationships should be 
        // set by the service layer, not directly by the mapper
        
        return payment;
    }
} 