package com.propertymanagement.repository;

import com.propertymanagement.model.User;
import com.propertymanagement.model.UserRole;
import com.propertymanagement.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByActive(boolean active);
    
    List<User> findByRoleAndActive(UserRole role, boolean active);
    
    List<User> findByFirstNameContainingOrLastNameContainingOrEmailContaining(String firstName, String lastName, String email);
    
    Optional<User> findByIdAndRole(Long id, UserRole role);
} 