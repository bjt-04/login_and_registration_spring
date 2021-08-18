package com.ben.login_and_registration_spring.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ben.login_and_registration_spring.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	 User findByEmail(String email);
	 List<User> findAll();
}