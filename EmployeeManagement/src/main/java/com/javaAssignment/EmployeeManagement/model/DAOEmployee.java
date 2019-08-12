package com.javaAssignment.EmployeeManagement.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "employee")
public class DAOEmployee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="EMAIL",unique=true)
	private String email;
	
	@Column(name="PASSWORD")
	@JsonIgnore
	private String password;
	
	
	public Integer getId() {
		return id;
	}

	/*public void setId(long id) {
		this.id = id;
	}*/

	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="LAST_NAME")
	private String lastName;
	
	@NotFound(action = NotFoundAction.IGNORE) 
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ManyToOne(cascade={CascadeType.MERGE},fetch = FetchType.LAZY)
	@JoinColumn(name="MANAGER_ID")
	private DAOEmployee manager;
	
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public DAOEmployee getManager() {
		return manager;
	}

	public void setManager(DAOEmployee manager) {
		this.manager = manager;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}