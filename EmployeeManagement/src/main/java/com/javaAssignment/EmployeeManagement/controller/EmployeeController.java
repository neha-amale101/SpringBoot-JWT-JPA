package com.javaAssignment.EmployeeManagement.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javaAssignment.EmployeeManagement.config.JwtTokenUtil;
import com.javaAssignment.EmployeeManagement.model.DAOEmployee;
import com.javaAssignment.EmployeeManagement.model.EmployeeAPIResponse;
import com.javaAssignment.EmployeeManagement.service.EmployeeService;

@RestController
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@RequestMapping({ "/hello" })
	public String firstPage() {
		return "Hello World";
	}

	/* Save Employee details for given manager */
	@RequestMapping(value = "/addEmployee", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DAOEmployee> addEmployee(@RequestHeader("Authorization") String authCode,
			@Valid @RequestBody DAOEmployee emp) throws URISyntaxException {

		String jwtToken;
		try {
			if (authCode != null && authCode.startsWith("Bearer ")) {
				jwtToken = authCode.substring(7);
				String userEmail = jwtTokenUtil.getUsernameFromToken(jwtToken);
				DAOEmployee manager = employeeService.findOneByEmail(userEmail);

				emp.setManager(manager);
				DAOEmployee result = employeeService.save(emp);
				return ResponseEntity.created(new URI("/addEmployee/" + result.getId())).body(result);
			} else {
				return new ResponseEntity<DAOEmployee>(HttpStatus.UNAUTHORIZED);
			}
		} catch (EntityExistsException e) {
			return new ResponseEntity<DAOEmployee>(HttpStatus.CONFLICT);
		}
	}

	/* Update an employee */
	@RequestMapping(value = "/updateEmployee", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DAOEmployee> updateEmployee(@RequestHeader("Authorization") String authCode,
			@RequestBody DAOEmployee empDetails) throws URISyntaxException {

		if (empDetails.getId() == null) {
			return new ResponseEntity<DAOEmployee>(HttpStatus.NOT_FOUND);
		}
		try {

			if (authCode != null && authCode.startsWith("Bearer ")) {
				String jwtToken = authCode.substring(7);
				String userEmail = jwtTokenUtil.getUsernameFromToken(jwtToken);
				DAOEmployee manager = employeeService.findOneByEmail(userEmail);

				empDetails.setManager(manager);
				DAOEmployee result = employeeService.update(empDetails);
				return ResponseEntity.created(new URI("/updateEmployee/" + result.getId())).body(result);
			} else {
				return new ResponseEntity<DAOEmployee>(HttpStatus.UNAUTHORIZED);
			}

		} catch (EntityNotFoundException e) {
			return new ResponseEntity<DAOEmployee>(HttpStatus.NOT_FOUND);

		}
	}

	/* Delete an Employee */
	@RequestMapping(value = "/deleteEmployee/{employeeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DAOEmployee> deleteEmployee(@PathVariable Integer employeeId) {

		if (employeeId == null) {
			System.out.println("emp is null");
			return new ResponseEntity<DAOEmployee>(HttpStatus.NOT_FOUND);
		}
		
		DAOEmployee empDetails = employeeService.findOneByEmployeeId(employeeId);
		if (empDetails == null) {
			return ResponseEntity.notFound().build();
		}
		employeeService.delete(empDetails);
		return ResponseEntity.ok().build();
	}

	/* Get one Employee based on Employee Id */
	@RequestMapping(value = "/getEmployee/{employeeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DAOEmployee> getEmployeeById(@PathVariable(value = "employeeId") Integer employeeId) {

		DAOEmployee emp = employeeService.findOneByEmployeeId(employeeId);
		if (emp == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(emp);
	}

	/* Get all Employee based on Manager Id */
	@RequestMapping(value = "/getAllEmployee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEmployeeById(@RequestHeader("Authorization") String authCode) {
		EmployeeAPIResponse<Set<DAOEmployee>> employeeAPIResponse = new EmployeeAPIResponse<Set<DAOEmployee>>();		
		String jwtToken;
		try {
			if (authCode != null && authCode.startsWith("Bearer ")) {
				jwtToken = authCode.substring(7);
				String userEmail = jwtTokenUtil.getUsernameFromToken(jwtToken);
				DAOEmployee manager = employeeService.findOneByEmail(userEmail);
				if (manager == null) {
					employeeAPIResponse.setMessage("This user does not exists!!");
					return ResponseEntity.notFound().build();
				}
				Set<DAOEmployee> empList = employeeService.findAllByManager(manager);
				if (empList == null || empList.size() == 0) {
					employeeAPIResponse.setMessage("This user does not have employees");
					return ResponseEntity.ok(employeeAPIResponse);
				} else {
					employeeAPIResponse.setMessage("Employee list for logged in Manager is as below");
					employeeAPIResponse.setData(empList);
					return ResponseEntity.ok(employeeAPIResponse);					
				}
			} else {
				employeeAPIResponse.setMessage("This user is unauthorized!! Please login");
				return ResponseEntity.ok(employeeAPIResponse);
			}
		} catch (Exception e) {
			employeeAPIResponse.setMessage("error occured:"+e.getMessage());
			return ResponseEntity.ok(employeeAPIResponse);
		}

	}

}