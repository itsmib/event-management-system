package com.cts.EventManagementSystem.model;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "users")
public class UserRegistration {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
 
    private String name;
 
    @Column(unique = true)
    private String email;
 
    private String password;
 
    private String contactNumber;
 
    private String role; // USER / ADMIN
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	
	public UserRegistration() {
	}

	public UserRegistration(Long userId, String name, String email, String password, String contactNumber,
			String role) {
		super();
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.password = password;
		this.contactNumber = contactNumber;
		this.role = role;
	}
	
	
}