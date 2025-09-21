package com.propertymanagement.controller;

import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.Payment;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import com.propertymanagement.service.PropertyService;
import com.propertymanagement.service.PaymentService;
import com.propertymanagement.service.MaintenanceRequestService;
import com.propertymanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @Autowired
    private UserService userService;

    @GetMapping("/landlord/dashboard/stats")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Map<String, Object>> getLandlordDashboardStats() {
        User currentUser = userService.getCurrentUser();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Get property count
        long propertyCount = propertyService.countPropertiesByLandlord(currentUser);
        stats.put("properties", propertyCount);
        
        // Get tenant count
        long tenantCount = userService.countTenantsByLandlord(currentUser);
        stats.put("tenants", tenantCount);
        
        // Get pending payments count
        long pendingPaymentsCount = paymentService.countPendingPaymentsByLandlord(currentUser);
        stats.put("pendingPayments", pendingPaymentsCount);
        
        // Get maintenance requests count
        int maintenanceRequestsCount = maintenanceRequestService.countPendingRequestsByLandlord(currentUser);
        stats.put("maintenanceRequests", maintenanceRequestsCount);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/landlord/properties")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<Property>> getLandlordProperties() {
        User currentUser = userService.getCurrentUser();
        // For now, return an empty list until we implement the actual service method
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    @GetMapping("/landlord/payments/recent")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<Payment>> getRecentPayments(
            @RequestParam(defaultValue = "5") int limit) {
        User currentUser = userService.getCurrentUser();
        List<Payment> payments = paymentService.findRecentPaymentsByLandlord(currentUser, limit);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/landlord/maintenance-requests/recent")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MaintenanceRequest>> getRecentMaintenanceRequests(
            @RequestParam(defaultValue = "5") int limit) {
        User currentUser = userService.getCurrentUser();
        List<MaintenanceRequest> requests = maintenanceRequestService.findRecentRequestsByLandlord(currentUser, limit);
        return ResponseEntity.ok(requests);
    }
} 