package com.javaAssignment.EmployeeManagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class EmployeeAPIResponse<T> {
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private T data;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String token;	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String message;
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
