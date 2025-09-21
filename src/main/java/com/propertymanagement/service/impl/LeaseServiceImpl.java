package com.propertymanagement.service.impl;

import com.propertymanagement.exception.ResourceNotFoundException;
import com.propertymanagement.model.Lease;
import com.propertymanagement.model.LeaseStatus;
import com.propertymanagement.model.LeaseType;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import com.propertymanagement.repository.LeaseRepository;
import com.propertymanagement.service.LeaseService;
import com.propertymanagement.service.base.impl.BaseServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaseServiceImpl extends BaseServiceImpl<Lease, LeaseRepository> implements LeaseService {

    public LeaseServiceImpl(LeaseRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Lease createLease(Lease lease, Property property, User tenant, User landlord) {
        if (hasActiveLease(property)) {
            throw new IllegalStateException("Property already has an active lease");
        }
        
        lease.setProperty(property);
        lease.setTenant(tenant);
        lease.setLandlord(landlord);
        lease.setStatus(LeaseStatus.PENDING_SIGNATURE);
        
        // Handle null values
        if (lease.getMonthlyRent() == null && property.getMonthlyRent() != null) {
            lease.setMonthlyRent(property.getMonthlyRent());
        }
        
        if (lease.getSecurityDeposit() == null && property.getMonthlyRent() != null) {
            // Default to 2 months rent for security deposit
            lease.setSecurityDeposit(property.getMonthlyRent().multiply(new java.math.BigDecimal(2)));
        }
        
        // Set default lease type if null
        if (lease.getLeaseType() == null) {
            lease.setLeaseType(LeaseType.FIXED_TERM);
        }
        
        // Set default payment due date if null
        if (lease.getPaymentDueDay() == null) {
            lease.setPaymentDueDay(1); // Default to 1st of month
        }
        
        return repository.save(lease);
    }

    @Override
    @Transactional
    public Lease updateLease(Long id, Lease lease) {
        Lease existingLease = findById(id);
        
        existingLease.setStartDate(lease.getStartDate());
        existingLease.setEndDate(lease.getEndDate());
        existingLease.setMonthlyRent(lease.getMonthlyRent());
        existingLease.setSecurityDeposit(lease.getSecurityDeposit());
        existingLease.setPetDeposit(lease.getPetDeposit());
        existingLease.setLeaseType(lease.getLeaseType());
        existingLease.setPaymentDueDay(lease.getPaymentDueDay());
        existingLease.setLateFeePercentage(lease.getLateFeePercentage());
        existingLease.setGracePeriodDays(lease.getGracePeriodDays());
        existingLease.setAutoRenew(lease.isAutoRenew());
        existingLease.setPetsAllowed(lease.isPetsAllowed());
        existingLease.setPetPolicy(lease.getPetPolicy());
        existingLease.setUtilitiesIncluded(lease.isUtilitiesIncluded());
        existingLease.setUtilitiesDetails(lease.getUtilitiesDetails());
        existingLease.setFurnished(lease.isFurnished());
        existingLease.setFurnishingDetails(lease.getFurnishingDetails());
        existingLease.setParkingIncluded(lease.isParkingIncluded());
        existingLease.setParkingDetails(lease.getParkingDetails());
        existingLease.setAdditionalTerms(lease.getAdditionalTerms());
        existingLease.setTerminationNoticeDays(lease.getTerminationNoticeDays());
        existingLease.setRenewalNoticeDays(lease.getRenewalNoticeDays());
        
        return repository.save(existingLease);
    }

    @Override
    @Transactional
    public void deleteLease(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Lease not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Page<Lease> findByProperty(Property property, Pageable pageable) {
        return repository.findByProperty(property, pageable);
    }

    @Override
    public Page<Lease> findByPropertyId(Long propertyId, Pageable pageable) {
        return repository.findByProperty_Id(propertyId, pageable);
    }

    @Override
    public Page<Lease> findByTenant(User tenant, Pageable pageable) {
        return repository.findByTenant(tenant, pageable);
    }

    @Override
    public Page<Lease> findByLandlord(User landlord, Pageable pageable) {
        return repository.findByLandlord(landlord, pageable);
    }

    @Override
    public Page<Lease> findByStatus(LeaseStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    @Override
    public Page<Lease> findByPropertyAndStatus(Property property, LeaseStatus status, Pageable pageable) {
        return repository.findByPropertyAndStatus(property, status, pageable);
    }

    @Override
    public Page<Lease> findByTenantAndStatus(User tenant, LeaseStatus status, Pageable pageable) {
        return repository.findByTenantAndStatus(tenant, status, pageable);
    }

    @Override
    public Page<Lease> findByLandlordAndStatus(User landlord, LeaseStatus status, Pageable pageable) {
        return repository.findByLandlordAndStatus(landlord, status, pageable);
    }

    @Override
    public List<Lease> findExpiringLeases(LocalDate startDate, LocalDate endDate) {
        return repository.findByEndDateBetween(startDate, endDate);
    }

    @Override
    public List<Lease> findExpiredLeases(LocalDate date) {
        return repository.findByStatusAndEndDateBefore(LeaseStatus.ACTIVE, date);
    }

    @Override
    public List<Lease> findUpcomingLeases(LocalDate date) {
        return repository.findByStatusAndStartDateAfter(LeaseStatus.PENDING_SIGNATURE, date);
    }

    @Override
    public Lease getCurrentLease(Property property) {
        return repository.findFirstByPropertyAndStatusOrderByStartDateDesc(property, LeaseStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active lease found for property"));
    }

    @Override
    public boolean hasActiveLease(Property property) {
        return repository.existsByPropertyAndStatus(property, LeaseStatus.ACTIVE);
    }

    @Override
    public long countActiveLeasesByProperty(Property property) {
        return repository.countByPropertyAndStatus(property, LeaseStatus.ACTIVE);
    }

    @Override
    public long countActiveLeasesByTenant(User tenant) {
        return repository.countByTenantAndStatus(tenant, LeaseStatus.ACTIVE);
    }

    @Override
    public long countActiveLeasesByLandlord(User landlord) {
        return repository.countByLandlordAndStatus(landlord, LeaseStatus.ACTIVE);
    }

    @Override
    @Transactional
    public Lease updateLeaseStatus(Long id, LeaseStatus status) {
        Lease lease = findById(id);
        lease.setStatus(status);
        return repository.save(lease);
    }

    @Override
    @Transactional
    public Lease renewLease(Long id, LocalDate newEndDate) {
        Lease lease = findById(id);
        if (!canRenewLease(id)) {
            throw new IllegalStateException("Lease cannot be renewed");
        }
        
        lease.setEndDate(newEndDate);
        lease.setStatus(LeaseStatus.ACTIVE);
        return repository.save(lease);
    }

    @Override
    @Transactional
    public Lease terminateLease(Long id, LocalDate terminationDate) {
        Lease lease = findById(id);
        if (!canTerminateLease(id)) {
            throw new IllegalStateException("Lease cannot be terminated");
        }
        
        lease.setEndDate(terminationDate);
        lease.setStatus(LeaseStatus.TERMINATED);
        return repository.save(lease);
    }

    @Override
    public boolean canTerminateLease(Long id) {
        Lease lease = findById(id);
        return lease.getStatus() == LeaseStatus.ACTIVE &&
               (lease.getTerminationNoticeDays() == null ||
                LocalDate.now().plusDays(lease.getTerminationNoticeDays()).isBefore(lease.getEndDate()));
    }

    @Override
    public boolean canRenewLease(Long id) {
        Lease lease = findById(id);
        return lease.getStatus() == LeaseStatus.ACTIVE &&
               lease.isAutoRenew() &&
               (lease.getRenewalNoticeDays() == null ||
                LocalDate.now().plusDays(lease.getRenewalNoticeDays()).isBefore(lease.getEndDate()));
    }
} 