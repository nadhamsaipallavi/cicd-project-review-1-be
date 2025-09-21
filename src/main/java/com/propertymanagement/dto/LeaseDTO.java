package com.propertymanagement.dto;

import com.propertymanagement.dto.base.BaseDTO;
import com.propertymanagement.model.LeaseStatus;
import com.propertymanagement.model.LeaseType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaseDTO extends BaseDTO {
    
    private Long propertyId;
    
    private String propertyTitle;
    
    private Long tenantId;
    
    private String tenantName;
    
    private Long landlordId;
    
    private String landlordName;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal monthlyRent;
    
    private BigDecimal securityDeposit;
    
    private BigDecimal petDeposit;
    
    private LeaseType leaseType;
    
    private Integer term;
    
    private Integer paymentDueDay;
    
    private BigDecimal lateFee;
    
    private String termsAndConditions;
    
    private LeaseStatus status;
    
    private Integer gracePeriodDays;
    
    private boolean autoRenew;
    
    private boolean petsAllowed;
    
    private String petPolicy;
    
    private boolean utilitiesIncluded;
    
    private String utilitiesDetails;
    
    private boolean furnished;
    
    private String furnishingDetails;
    
    private boolean parkingIncluded;
    
    private String parkingDetails;
    
    private String additionalTerms;
    
    private Integer terminationNoticeDays;
    
    private Integer renewalNoticeDays;
    
    // Getters and setters
    
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public String getPropertyTitle() {
        return propertyTitle;
    }
    
    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }
    
    public Long getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public Long getLandlordId() {
        return landlordId;
    }
    
    public void setLandlordId(Long landlordId) {
        this.landlordId = landlordId;
    }
    
    public String getLandlordName() {
        return landlordName;
    }
    
    public void setLandlordName(String landlordName) {
        this.landlordName = landlordName;
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
    
    public LeaseType getLeaseType() {
        return leaseType;
    }
    
    public void setLeaseType(LeaseType leaseType) {
        this.leaseType = leaseType;
    }
    
    public Integer getTerm() {
        return term;
    }
    
    public void setTerm(Integer term) {
        this.term = term;
    }
    
    public Integer getPaymentDueDay() {
        return paymentDueDay;
    }
    
    public void setPaymentDueDay(Integer paymentDueDay) {
        this.paymentDueDay = paymentDueDay;
    }
    
    public BigDecimal getLateFee() {
        return lateFee;
    }
    
    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }
    
    public String getTermsAndConditions() {
        return termsAndConditions;
    }
    
    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
    
    public LeaseStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaseStatus status) {
        this.status = status;
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
} 