package com.propertymanagement.repository;

import com.propertymanagement.model.Property;
import com.propertymanagement.model.PropertyType;
import com.propertymanagement.model.User;
import com.propertymanagement.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends BaseRepository<Property> {
    
    List<Property> findByLandlord(User landlord);
    
    Page<Property> findByLandlord(User landlord, Pageable pageable);
    
    Page<Property> findByAvailableTrue(Pageable pageable);
    
    Page<Property> findByAvailableTrueAndActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.available = true AND p.active = true " +
           "AND (:city IS NULL OR p.city LIKE %:city%) " +
           "AND (:state IS NULL OR p.state LIKE %:state%) " +
           "AND (:minPrice IS NULL OR p.monthlyRent >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.monthlyRent <= :maxPrice) " +
           "AND (:bedrooms IS NULL OR p.numberOfBedrooms >= :bedrooms) " +
           "AND (:bathrooms IS NULL OR p.numberOfBathrooms >= :bathrooms) " +
           "AND (:propertyType IS NULL OR p.propertyType = :propertyType)")
    Page<Property> searchProperties(
            @Param("city") String city,
            @Param("state") String state,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("bedrooms") Integer bedrooms,
            @Param("bathrooms") Integer bathrooms,
            @Param("propertyType") PropertyType propertyType,
            Pageable pageable);
    
    @Query("SELECT DISTINCT p.city FROM Property p WHERE p.available = true AND p.active = true ORDER BY p.city")
    List<String> findDistinctCities();
    
    @Query("SELECT DISTINCT p.state FROM Property p WHERE p.available = true AND p.active = true ORDER BY p.state")
    List<String> findDistinctStates();
    
    @Query("SELECT DISTINCT p.propertyType FROM Property p WHERE p.available = true AND p.active = true ORDER BY p.propertyType")
    List<String> findDistinctPropertyTypes();
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.landlord = :landlord")
    Long countByLandlord(@Param("landlord") User landlord);
    
    Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);
    
    Page<Property> findByMonthlyRentBetween(BigDecimal minRent, BigDecimal maxRent, Pageable pageable);
    
    Page<Property> findByNumberOfBedrooms(Integer numberOfBedrooms, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndPropertyType(PropertyType propertyType, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndMonthlyRentLessThanEqual(BigDecimal maxRent, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndNumberOfBedroomsGreaterThanEqual(Integer minBedrooms, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndPropertyTypeAndMonthlyRentLessThanEqualAndNumberOfBedroomsGreaterThanEqual(
            PropertyType propertyType, BigDecimal maxRent, Integer minBedrooms, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndPropertyTypeAndMonthlyRentLessThanEqual(
            PropertyType propertyType, BigDecimal maxRent, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndPropertyTypeAndNumberOfBedroomsGreaterThanEqual(
            PropertyType propertyType, Integer minBedrooms, Pageable pageable);
    
    Page<Property> findByAvailableTrueAndMonthlyRentLessThanEqualAndNumberOfBedroomsGreaterThanEqual(
            BigDecimal maxRent, Integer minBedrooms, Pageable pageable);
    
    List<Property> findByFeaturedTrueAndAvailableTrue();
    
    Optional<Property> findByIdAndLandlord(Long id, User landlord);
    
    boolean existsByIdAndLandlord(Long id, User landlord);
    
    long countByAvailableTrue();
} 