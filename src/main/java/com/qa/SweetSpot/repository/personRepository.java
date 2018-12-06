package com.qa.SweetSpot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qa.SweetSpot.mySpringBootDatabaseApp.*;

@Repository
public interface personRepository extends JpaRepository<Users,String> {
	
	Users findByusername(@Param("username") String username);
}
