package com.campaign_management.campaign_management.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CompositeKeyCustomer implements Serializable {
	
	@Column(name="customer_email")
	private String customer_email;
	
	@Column(name="customer_phone")
	private Long customer_phone;
	
	public CompositeKeyCustomer() {
		
	}
	
	public CompositeKeyCustomer(String customer_email, Long customer_phone) {
		super();
		this.customer_email = customer_email;
		this.customer_phone = customer_phone;
	}

	public String getCustomer_email() {
		return customer_email;
	}

	public void setCustomer_email(String customer_email) {
		this.customer_email = customer_email;
	}

	public Long getCustomer_phone() {
		return customer_phone;
	}

	public void setCustomer_phone(Long customer_phone) {
		this.customer_phone = customer_phone;
	}

}
