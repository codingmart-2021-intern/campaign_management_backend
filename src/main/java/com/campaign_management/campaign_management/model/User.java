package com.campaign_management.campaign_management.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_tbl")
public class User {

	@Id
	@GeneratedValue
	public int id;

	public String image;
	public String name;
	public String email;
	public Long phone;
	public String password;
	public Date dob;
	public String role;

	@Column(name = "EnabledAccount")
	public boolean enabled;

	public String verificationCode;
	public boolean mbverify;
	public String gender;
	public String otp;
	public Long timestamp;

	// Getter and Setter

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Date getDOB() {
		return dob;
	}

	public void setDOB(Date dOB) {
		dob = dOB;
	}

	public void setMbverify(boolean mbverify) {
		this.mbverify = mbverify;
	}

	public boolean getMbVerify() {
		return mbverify;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
