package com.propertymanagement.controller;

import com.propertymanagement.dto.LeaseDTO;
import com.propertymanagement.dto.LeaseDocumentDTO;
import com.propertymanagement.dto.LeaseRenewalDTO;
import com.propertymanagement.dto.LeaseStatusUpdateDTO;
import com.propertymanagement.dto.LeaseTerminationDTO;
import com.propertymanagement.mapper.LeaseMapper;
import com.propertymanagement.model.Lease;
import com.propertymanagement.model.LeaseStatus;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import com.propertymanagement.service.LeaseService;
import com.propertymanagement.service.PropertyService;
import com.propertymanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeaseController {

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private PropertyService propertyService;

    @Autowired
    private LeaseMapper leaseMapper;

    // Get all leases (admin)
    @GetMapping("/leases")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LeaseDTO> getAllLeases(Pageable pageable) {
        return leaseService.findAll(pageable)
                .map(leaseMapper::toDTO);
    }

    // Get leases by property (landlord)
    @GetMapping("/leases/property/{propertyId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<LeaseDTO> getLeasesByProperty(
            @PathVariable Long propertyId, Pageable pageable) {
        return leaseService.findByPropertyId(propertyId, pageable)
                .map(leaseMapper::toDTO);
    }

    // Get leases for landlord
    @GetMapping("/landlord/leases")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<LeaseDTO> getLandlordLeases(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return leaseService.findByLandlord(currentUser, pageable)
                .map(leaseMapper::toDTO);
    }

    // Get active leases for tenant (plural endpoint)
    @GetMapping("/tenant/leases")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<LeaseDTO>> getTenantLeases() {
        try {
            User currentUser = userService.getCurrentUser();
            // Find all leases for the current tenant
            Page<Lease> leasesPage = leaseService.findByTenant(currentUser, Pageable.unpaged());
            
            if (leasesPage.isEmpty()) {
                // Return an empty list instead of 404 when no leases found
                return ResponseEntity.ok(List.of());
            }
            
            List<LeaseDTO> leasesList = leasesPage.getContent()
                    .stream()
                    .map(leaseMapper::toDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(leasesList);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error retrieving tenant leases: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get active lease for tenant (singular endpoint)
    @GetMapping("/tenant/lease")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<LeaseDTO> getTenantLease() {
        try {
            User currentUser = userService.getCurrentUser();
            // Find the active lease for the current user by finding active leases with this tenant
            Page<Lease> activeLeasesPage = leaseService.findByTenantAndStatus(currentUser, LeaseStatus.ACTIVE, Pageable.unpaged());
            
            if (activeLeasesPage.isEmpty()) {
                // If no ACTIVE lease found, try to find any other lease
                Page<Lease> anyLeasePage = leaseService.findByTenant(currentUser, Pageable.unpaged());
                if (anyLeasePage.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                
                // Return the most recent lease of any status
                Lease lease = anyLeasePage.getContent().get(0);
                return ResponseEntity.ok(leaseMapper.toDTO(lease));
            }
            
            // Return the most recent active lease
            Lease lease = activeLeasesPage.getContent().get(0);
            return ResponseEntity.ok(leaseMapper.toDTO(lease));
        } catch (Exception e) {
            // Log the error
            System.err.println("Error retrieving tenant lease: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get lease for landlord by ID 
    @GetMapping("/landlord/leases/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<LeaseDTO> getLandlordLeaseById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(id);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be landlord of this property
        boolean hasAccess = lease.getLandlord().getId().equals(currentUser.getId());
        
        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(leaseMapper.toDTO(lease));
    }

    // Get lease by ID
    @GetMapping("/leases/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<LeaseDTO> getLeaseById(@PathVariable Long id) {
        Lease lease = leaseService.findById(id);
        User currentUser = userService.getCurrentUser();
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin, landlord of this property, or tenant of this lease
        boolean hasAccess = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId()) ||
                           lease.getTenant().getId().equals(currentUser.getId());
        
        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(leaseMapper.toDTO(lease));
    }

    // Create a new lease (landlord)
    @PostMapping("/leases")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<LeaseDTO> createLease(@RequestBody LeaseDTO leaseDTO) {
        User currentUser = userService.getCurrentUser();
        Lease leaseToCreate = leaseMapper.toEntity(leaseDTO);
        
        // Get property and tenant from DTO
        Property property = null;
        User tenant = null;
        
        if (leaseDTO.getPropertyId() != null) {
            property = propertyService.findById(leaseDTO.getPropertyId());
            if (property == null) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        if (leaseDTO.getTenantId() != null) {
            tenant = userService.findById(leaseDTO.getTenantId());
            if (tenant == null) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        if (property == null || tenant == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Lease lease = leaseService.createLease(leaseToCreate, property, tenant, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(leaseMapper.toDTO(lease));
    }

    // Create a new lease request (tenant)
    @PostMapping("/tenant/leases")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<LeaseDTO> createTenantLeaseRequest(@RequestBody LeaseDTO leaseDTO) {
        try {
            User currentUser = userService.getCurrentUser();
            Lease leaseToCreate = leaseMapper.toEntity(leaseDTO);
            
            // Get property from DTO
            Property property = null;
            
            if (leaseDTO.getPropertyId() != null) {
                property = propertyService.findById(leaseDTO.getPropertyId());
                if (property == null) {
                    return ResponseEntity.badRequest().body(null);
                }
            } else {
                return ResponseEntity.badRequest().body(null);
            }
            
            // Set tenant to current user
            User tenant = currentUser;
            
            // Get landlord from property
            User landlord = property.getLandlord();
            
            if (property == null || landlord == null) {
                return ResponseEntity.badRequest().body(null);
            }
            
            // Handle null values for pricing
            if (leaseToCreate.getMonthlyRent() == null && property.getMonthlyRent() != null) {
                leaseToCreate.setMonthlyRent(property.getMonthlyRent());
            }
            
            if (leaseToCreate.getSecurityDeposit() == null && property.getMonthlyRent() != null) {
                // Default to 2 months rent for security deposit if not specified
                leaseToCreate.setSecurityDeposit(property.getMonthlyRent().multiply(new java.math.BigDecimal(2)));
            }
            
            Lease lease = leaseService.createLease(leaseToCreate, property, tenant, landlord);
            return ResponseEntity.status(HttpStatus.CREATED).body(leaseMapper.toDTO(lease));
        } catch (Exception e) {
            // Log the error
            System.err.println("Error creating tenant lease: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update lease
    @PutMapping("/leases/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<LeaseDTO> updateLease(
            @PathVariable Long id, @RequestBody LeaseDTO leaseDTO) {
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(id);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin or landlord of this property
        boolean canModify = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId());
        
        if (!canModify) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Lease leaseToUpdate = leaseMapper.toEntity(leaseDTO);
        Lease updatedLease = leaseService.updateLease(id, leaseToUpdate);
        return ResponseEntity.ok(leaseMapper.toDTO(updatedLease));
    }

    // Update lease status
    @PutMapping("/leases/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<LeaseDTO> updateLeaseStatus(
            @PathVariable Long id, @RequestBody LeaseStatusUpdateDTO statusUpdate) {
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(id);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin or landlord of this property
        boolean canModify = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId());
        
        if (!canModify) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Lease updatedLease = leaseService.updateLeaseStatus(id, statusUpdate.getStatus());
        return ResponseEntity.ok(leaseMapper.toDTO(updatedLease));
    }

    // Terminate lease
    @PutMapping("/leases/{id}/terminate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<LeaseDTO> terminateLease(
            @PathVariable Long id, @RequestBody LeaseTerminationDTO terminationData) {
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(id);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin or landlord of this property
        boolean canModify = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId());
        
        if (!canModify) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Lease terminatedLease = leaseService.terminateLease(id, terminationData.getTerminationDate());
        
        return ResponseEntity.ok(leaseMapper.toDTO(terminatedLease));
    }

    // Renew lease
    @PutMapping("/leases/{id}/renew")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<LeaseDTO> renewLease(
            @PathVariable Long id, @RequestBody LeaseRenewalDTO renewalData) {
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(id);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin or landlord of this property
        boolean canModify = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId());
        
        if (!canModify) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Lease renewedLease = leaseService.renewLease(id, renewalData.getEndDate());
        
        return ResponseEntity.ok(leaseMapper.toDTO(renewedLease));
    }

    // Get lease documents
    @GetMapping("/leases/{leaseId}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<List<LeaseDocumentDTO>> getLeaseDocuments(@PathVariable Long leaseId) {
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(leaseId);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin, landlord or tenant of this lease
        boolean hasAccess = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId()) ||
                           lease.getTenant().getId().equals(currentUser.getId());
        
        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Since we don't have direct support in LeaseService, we'll need to implement a workaround
        // This is a temporary stub - actual implementation would depend on how lease documents are managed
        return ResponseEntity.ok(List.of());
    }

    // Upload lease document
    @PostMapping(value = "/leases/{leaseId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<LeaseDocumentDTO> uploadLeaseDocument(
            @PathVariable Long leaseId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("documentType") String documentType) {
        
        User currentUser = userService.getCurrentUser();
        Lease lease = leaseService.findById(leaseId);
        
        if (lease == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights - user must be admin or landlord of this property
        boolean canModify = currentUser.getRole().equals("ROLE_ADMIN") || 
                           lease.getLandlord().getId().equals(currentUser.getId());
        
        if (!canModify) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Since we don't have direct support in LeaseService, we'll need to implement a workaround
        // This is a temporary stub - actual implementation would depend on how lease documents are managed
        LeaseDocumentDTO documentDTO = new LeaseDocumentDTO();
        documentDTO.setId(1L);
        documentDTO.setFileName(file.getOriginalFilename());
        documentDTO.setDocumentType(documentType);
        documentDTO.setUrl("https://example.com/documents/1");
        documentDTO.setUploadedAt(java.time.LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(documentDTO);
    }

    // Get upcoming lease renewals (for landlord dashboard)
    @GetMapping("/landlord/leases/upcoming-renewals")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<LeaseDTO>> getUpcomingLeaseRenewals(
            @RequestParam(defaultValue = "30") int daysThreshold,
            @RequestParam(defaultValue = "5") int limit) {
        
        User currentUser = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        
        // Find leases that are approaching their end date
        List<Lease> expiringLeases = leaseService.findExpiringLeases(today, thresholdDate);
        
        // Filter to only include the current user's leases
        List<Lease> userLeases = expiringLeases.stream()
            .filter(lease -> lease.getLandlord().getId().equals(currentUser.getId()))
            .limit(limit)
            .toList();
        
        return ResponseEntity.ok(leaseMapper.toDTOList(userLeases));
    }

    // Get expiring leases (for landlord dashboard)
    @GetMapping("/landlord/leases/expiring")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<LeaseDTO>> getExpiringLeases(
            @RequestParam(defaultValue = "30") int daysThreshold,
            @RequestParam(defaultValue = "5") int limit) {
        
        User currentUser = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        
        // Find leases that are approaching their end date
        List<Lease> expiringLeases = leaseService.findExpiringLeases(today, thresholdDate);
        
        // Filter to only include the current user's leases
        List<Lease> userLeases = expiringLeases.stream()
            .filter(lease -> lease.getLandlord().getId().equals(currentUser.getId()))
            .limit(limit)
            .toList();
        
        return ResponseEntity.ok(leaseMapper.toDTOList(userLeases));
    }
    
    // DTOs for request handling
    static class LeaseStatusUpdateDTO {
        private LeaseStatus status;
        
        public LeaseStatus getStatus() {
            return status;
        }
        
        public void setStatus(LeaseStatus status) {
            this.status = status;
        }
    }
    
    static class LeaseTerminationDTO {
        private java.time.LocalDate terminationDate;
        private String reason;
        
        public java.time.LocalDate getTerminationDate() {
            return terminationDate;
        }
        
        public void setTerminationDate(java.time.LocalDate terminationDate) {
            this.terminationDate = terminationDate;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
    
    static class LeaseRenewalDTO {
        private java.time.LocalDate startDate;
        private java.time.LocalDate endDate;
        private java.math.BigDecimal monthlyRent;
        
        public java.time.LocalDate getStartDate() {
            return startDate;
        }
        
        public void setStartDate(java.time.LocalDate startDate) {
            this.startDate = startDate;
        }
        
        public java.time.LocalDate getEndDate() {
            return endDate;
        }
        
        public void setEndDate(java.time.LocalDate endDate) {
            this.endDate = endDate;
        }
        
        public java.math.BigDecimal getMonthlyRent() {
            return monthlyRent;
        }
        
        public void setMonthlyRent(java.math.BigDecimal monthlyRent) {
            this.monthlyRent = monthlyRent;
        }
    }
    
    static class LeaseDocumentDTO {
        private Long id;
        private String fileName;
        private String documentType;
        private String url;
        private java.time.LocalDateTime uploadedAt;
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public String getDocumentType() {
            return documentType;
        }
        
        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public java.time.LocalDateTime getUploadedAt() {
            return uploadedAt;
        }
        
        public void setUploadedAt(java.time.LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
        }
    }
} 