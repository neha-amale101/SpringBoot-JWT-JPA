package com.javaAssignment.EmployeeManagement.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javaAssignment.EmployeeManagement.model.DAOEmployee;

public interface EmployeeRepository extends JpaRepository<DAOEmployee, Integer>{	

	DAOEmployee findByEmail(String email);
	Set<DAOEmployee> findAllByManager(DAOEmployee manager);
}
