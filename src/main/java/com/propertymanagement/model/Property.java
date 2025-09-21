package com.propertymanagement.model;

import com.propertymanagement.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "properties")
@EqualsAndHashCode(callSuper = true)
public class Property extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(name = "property_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;
    
    @Column(name = "listing_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ListingType listingType = ListingType.FOR_RENT;
    
    @Column(name = "total_area")
    private Double totalArea;
    
    @Column(name = "number_of_bedrooms")
    private Integer numberOfBedrooms;
    
    @Column(name = "number_of_bathrooms")
    private Integer numberOfBathrooms;
    
    @Column(name = "monthly_rent")
    private BigDecimal monthlyRent;
    
    @Column(name = "sale_price")
    private BigDecimal salePrice;
    
    @Column(name = "security_deposit")
    private BigDecimal securityDeposit;
    
    @Column(name = "available_from")
    private java.time.LocalDate availableFrom;
    
    @Column(name = "is_available", nullable = false)
    private boolean available = true;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    @Column(name = "is_featured")
    private boolean featured = false;
    
    @ElementCollection
    @CollectionTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "amenity")
    private List<String> amenities;
    
    @ElementCollection
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url")
    private List<String> images;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Lease> leases;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<MaintenanceRequest> maintenanceRequests;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyInspection> inspections;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyPurchaseRequest> purchaseRequests;
    
    // Manual getters and setters to avoid Lombok issues
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public PropertyType getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
    
    public ListingType getListingType() {
        return listingType;
    }
    
    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }
    
    public Double getTotalArea() {
        return totalArea;
    }
    
    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }
    
    public Integer getNumberOfBedrooms() {
        return numberOfBedrooms;
    }
    
    public void setNumberOfBedrooms(Integer numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }
    
    public Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }
    
    public void setNumberOfBathrooms(Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }
    
    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }
    
    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
    
    public BigDecimal getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public java.time.LocalDate getAvailableFrom() {
        return availableFrom;
    }
    
    public void setAvailableFrom(java.time.LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isFeatured() {
        return featured;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public List<String> getAmenities() {
        return amenities;
    }
    
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public User getLandlord() {
        return landlord;
    }
    
    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }
    
    public List<Lease> getLeases() {
        return leases;
    }
    
    public void setLeases(List<Lease> leases) {
        this.leases = leases;
    }
    
    public List<MaintenanceRequest> getMaintenanceRequests() {
        return maintenanceRequests;
    }
    
    public void setMaintenanceRequests(List<MaintenanceRequest> maintenanceRequests) {
        this.maintenanceRequests = maintenanceRequests;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    
    public List<PropertyInspection> getInspections() {
        return inspections;
    }
    
    public void setInspections(List<PropertyInspection> inspections) {
        this.inspections = inspections;
    }
    
    public List<PropertyPurchaseRequest> getPurchaseRequests() {
        return purchaseRequests;
    }
    
    public void setPurchaseRequests(List<PropertyPurchaseRequest> purchaseRequests) {
        this.purchaseRequests = purchaseRequests;
    }
} 