package com.javaAssignment.EmployeeManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javaAssignment.EmployeeManagement.config.JwtTokenUtil;
import com.javaAssignment.EmployeeManagement.model.JwtRequest;
import com.javaAssignment.EmployeeManagement.model.JwtResponse;
import com.javaAssignment.EmployeeManagement.model.DAOEmployee;
import com.javaAssignment.EmployeeManagement.model.EmployeeAPIResponse;
import com.javaAssignment.EmployeeManagement.model.EmployeeDTO;
import com.javaAssignment.EmployeeManagement.service.JwtUserDetailsService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	// request to login and generate auth token as response
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		final String token = jwtTokenUtil.generateToken(userDetails);
		EmployeeAPIResponse<DAOEmployee> employeeAPIResponse = new EmployeeAPIResponse<DAOEmployee>();
		employeeAPIResponse.setToken(token);
		employeeAPIResponse.setMessage("User logged in successfully!!");
		return ResponseEntity.ok(employeeAPIResponse);
	}

	// request to register new manager
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody EmployeeDTO user) throws Exception {
		DAOEmployee savedEmployee = userDetailsService.save(user);
		EmployeeAPIResponse<DAOEmployee> employeeAPIResponse = new EmployeeAPIResponse<DAOEmployee>();
		if (savedEmployee != null && savedEmployee.getId() != null) {
			employeeAPIResponse.setData(savedEmployee);
			employeeAPIResponse.setMessage("User registered successfully!!");

		} else
			employeeAPIResponse.setMessage("User with same email id already exists");
		return ResponseEntity.ok(employeeAPIResponse);
	}

	private void authenticate(String email, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
