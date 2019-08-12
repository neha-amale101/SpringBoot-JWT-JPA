package com.javaAssignment.EmployeeManagement.service;

import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaAssignment.EmployeeManagement.model.DAOEmployee;
import com.javaAssignment.EmployeeManagement.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	EmployeeRepository employeeRepository;
	
	/* Save an employee*/	
	public DAOEmployee save(DAOEmployee emp) {
		
		if (emp.getId() != null && employeeRepository.existsById(emp.getId())) {
			throw new EntityExistsException("There is already existing employee with such ID in the database.");
		}
		return employeeRepository.save(emp);
	}
	
	/* To update Employee Details*/
	public DAOEmployee update(DAOEmployee employee) {

		if (employee.getId() != null && !employeeRepository.existsById(employee.getId())) {

			throw new EntityNotFoundException("There is no employee with such ID in the database.");
		}
		return employeeRepository.save(employee);
	}	
	
	/*Delete an employee*/
	public void delete(DAOEmployee emp) {		
		employeeRepository.delete(emp);
	}
	
	/*Find one employee by EmployeeId*/
	public DAOEmployee findOneByEmployeeId(Integer employeeId) {
		DAOEmployee emp = null;
		Optional<DAOEmployee> optEmp = employeeRepository.findById(employeeId);
		if(optEmp.isPresent())
			emp = optEmp.get();
		return emp;
	}
	
	/* Find all employee of manager*/
	public Set<DAOEmployee> findAllByManager(DAOEmployee manager){		
		return employeeRepository.findAllByManager(manager);
	}
	
	/*Find employee by Email*/
	public DAOEmployee findOneByEmail(String email) {
		DAOEmployee emp = employeeRepository.findByEmail(email);
		return emp;
	}
	
}
