package com.javaAssignment.EmployeeManagement.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.javaAssignment.EmployeeManagement.model.DAOEmployee;
import com.javaAssignment.EmployeeManagement.model.EmployeeDTO;
import com.javaAssignment.EmployeeManagement.repository.EmployeeRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		DAOEmployee user = employeeRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + email);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				(new ArrayList<>()));
	}

	
	public DAOEmployee save(EmployeeDTO user) {
		DAOEmployee newUser = new DAOEmployee();
		DAOEmployee savedUser = new DAOEmployee();
		
		try { // Check if user with same email exists or not
			DAOEmployee findUser = employeeRepository.findByEmail(user.getEmail());
			//No such user add it
			if ( findUser == null) {
				System.out.println("No such user");
				System.out.println("user Email:" + user.getEmail());
				System.out.println("user Id:" + user.getId());
				newUser.setEmail(user.getEmail());
				newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
				newUser.setFirstName(user.getFirstName());
				newUser.setLastName(user.getLastName());
				savedUser =  employeeRepository.save(newUser);
			}
		} catch (NonUniqueResultException e) {

		}
		return savedUser;
	}

}
