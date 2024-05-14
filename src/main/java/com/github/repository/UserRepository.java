package com.github.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	boolean existsByDocument(String document);
	boolean existsByEmail(String email);

}
