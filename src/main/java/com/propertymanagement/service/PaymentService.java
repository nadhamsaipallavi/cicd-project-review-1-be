package com.propertymanagement.service;

import com.propertymanagement.dto.NewCardDTO;
import com.propertymanagement.dto.PaymentMethodDTO;
import com.propertymanagement.model.Payment;
import com.propertymanagement.model.User;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    
    // Find methods
    Page<Payment> findAll(Pageable pageable);
    
    Payment findById(Long id);
    
    Page<Payment> findByPropertyId(Long propertyId, User currentUser, Pageable pageable);
    
    Page<Payment> findByLandlord(User landlord, Pageable pageable);
    
    Page<Payment> findByTenant(User tenant, Pageable pageable);
    
    List<Payment> findUpcomingPaymentsByTenant(User tenant);
    
    List<Payment> findRecentPayments(int limit);
    
    List<Payment> findRecentPaymentsByLandlord(User landlord, int limit);
    
    // Access control
    boolean canAccess(Payment payment, User user);
    
    // Payment processing
    Payment makePayment(BigDecimal amount, String description, Long leaseId,
                      Long paymentMethodId, User tenant);
    
    Payment makePaymentWithNewCard(BigDecimal amount, String description, Long leaseId,
                                 NewCardDTO newCard, boolean saveCard, User tenant);
    
    Payment recordPayment(BigDecimal amount, String description, Long leaseId,
                       String paymentMethod, LocalDate paymentDate, User user);
    
    Payment generateInvoice(Long leaseId, BigDecimal amount, String description,
                         LocalDate dueDate, User user);
    
    // Razorpay payment processing
    Payment recordRazorpayPayment(Payment payment);
    
    // Receipt generation
    Resource generateReceipt(Long paymentId);
    
    // Statistics
    Map<String, Object> getPaymentStatsByLandlord(User landlord);
    
    // Count pending payments for landlord dashboard
    long countPendingPaymentsByLandlord(User landlord);
    
    // Payment methods
    List<PaymentMethodDTO> findPaymentMethodsByUser(User user);
    
    PaymentMethodDTO addPaymentMethod(PaymentMethodDTO paymentMethodDTO, User user);
    
    boolean removePaymentMethod(Long paymentMethodId, User user);
    
    PaymentMethodDTO setDefaultPaymentMethod(Long paymentMethodId, User user);
} 