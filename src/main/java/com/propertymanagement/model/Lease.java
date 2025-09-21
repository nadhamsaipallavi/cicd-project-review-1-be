package com.propertymanagement.model;

import com.propertymanagement.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "leases")
@EqualsAndHashCode(callSuper = true)
public class Lease extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "monthly_rent", nullable = false)
    private BigDecimal monthlyRent;
    
    @Column(name = "security_deposit")
    private BigDecimal securityDeposit;
    
    @Column(name = "pet_deposit")
    private BigDecimal petDeposit;
    
    @Column(name = "lease_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaseType leaseType;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaseStatus status;
    
    @Column(name = "payment_due_day")
    private Integer paymentDueDay;
    
    @Column(name = "late_fee_percentage")
    private BigDecimal lateFeePercentage;
    
    @Column(name = "grace_period_days")
    private Integer gracePeriodDays;
    
    @Column(name = "auto_renew")
    private boolean autoRenew = false;
    
    @Column(name = "pets_allowed")
    private boolean petsAllowed = false;
    
    @Column(name = "pet_policy", length = 1000)
    private String petPolicy;
    
    @Column(name = "utilities_included")
    private boolean utilitiesIncluded = false;
    
    @Column(name = "utilities_details", length = 1000)
    private String utilitiesDetails;
    
    @Column(name = "furnished")
    private boolean furnished = false;
    
    @Column(name = "furnishing_details", length = 1000)
    private String furnishingDetails;
    
    @Column(name = "parking_included")
    private boolean parkingIncluded = false;
    
    @Column(name = "parking_details", length = 1000)
    private String parkingDetails;
    
    @Column(name = "additional_terms", length = 2000)
    private String additionalTerms;
    
    @Column(name = "termination_notice_days")
    private Integer terminationNoticeDays;
    
    @Column(name = "renewal_notice_days")
    private Integer renewalNoticeDays;
    
    @OneToMany(mappedBy = "lease", cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    // Manual getters and setters to avoid Lombok issues
    
    public Property getProperty() {
        return property;
    }
    
    public void setProperty(Property property) {
        this.property = property;
    }
    
    public User getTenant() {
        return tenant;
    }
    
    public void setTenant(User tenant) {
        this.tenant = tenant;
    }
    
    public User getLandlord() {
        return landlord;
    }
    
    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }
    
    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public BigDecimal getPetDeposit() {
        return petDeposit;
    }
    
    public void setPetDeposit(BigDecimal petDeposit) {
        this.petDeposit = petDeposit;
    }
    
    public LeaseType getLeaseType() {
        return leaseType;
    }
    
    public void setLeaseType(LeaseType leaseType) {
        this.leaseType = leaseType;
    }
    
    public LeaseStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaseStatus status) {
        this.status = status;
    }
    
    public Integer getPaymentDueDay() {
        return paymentDueDay;
    }
    
    public void setPaymentDueDay(Integer paymentDueDay) {
        this.paymentDueDay = paymentDueDay;
    }
    
    public BigDecimal getLateFeePercentage() {
        return lateFeePercentage;
    }
    
    public void setLateFeePercentage(BigDecimal lateFeePercentage) {
        this.lateFeePercentage = lateFeePercentage;
    }
    
    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }
    
    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }
    
    public boolean isAutoRenew() {
        return autoRenew;
    }
    
    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
    
    public boolean isPetsAllowed() {
        return petsAllowed;
    }
    
    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }
    
    public String getPetPolicy() {
        return petPolicy;
    }
    
    public void setPetPolicy(String petPolicy) {
        this.petPolicy = petPolicy;
    }
    
    public boolean isUtilitiesIncluded() {
        return utilitiesIncluded;
    }
    
    public void setUtilitiesIncluded(boolean utilitiesIncluded) {
        this.utilitiesIncluded = utilitiesIncluded;
    }
    
    public String getUtilitiesDetails() {
        return utilitiesDetails;
    }
    
    public void setUtilitiesDetails(String utilitiesDetails) {
        this.utilitiesDetails = utilitiesDetails;
    }
    
    public boolean isFurnished() {
        return furnished;
    }
    
    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }
    
    public String getFurnishingDetails() {
        return furnishingDetails;
    }
    
    public void setFurnishingDetails(String furnishingDetails) {
        this.furnishingDetails = furnishingDetails;
    }
    
    public boolean isParkingIncluded() {
        return parkingIncluded;
    }
    
    public void setParkingIncluded(boolean parkingIncluded) {
        this.parkingIncluded = parkingIncluded;
    }
    
    public String getParkingDetails() {
        return parkingDetails;
    }
    
    public void setParkingDetails(String parkingDetails) {
        this.parkingDetails = parkingDetails;
    }
    
    public String getAdditionalTerms() {
        return additionalTerms;
    }
    
    public void setAdditionalTerms(String additionalTerms) {
        this.additionalTerms = additionalTerms;
    }
    
    public Integer getTerminationNoticeDays() {
        return terminationNoticeDays;
    }
    
    public void setTerminationNoticeDays(Integer terminationNoticeDays) {
        this.terminationNoticeDays = terminationNoticeDays;
    }
    
    public Integer getRenewalNoticeDays() {
        return renewalNoticeDays;
    }
    
    public void setRenewalNoticeDays(Integer renewalNoticeDays) {
        this.renewalNoticeDays = renewalNoticeDays;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
} 