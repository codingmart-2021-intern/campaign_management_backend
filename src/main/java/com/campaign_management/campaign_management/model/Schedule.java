package com.campaign_management.campaign_management.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="schedule")
public class Schedule {
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@OneToOne(fetch = FetchType.EAGER, targetEntity=Offer.class)
	@JoinColumn(name="offer_id")
	private Offer offer_id;

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@OneToOne(fetch = FetchType.EAGER, targetEntity=User.class)
	@JoinColumn(name="user_id")
	private User user_id;
	
	@Id
	@Column(name="scheduled_at")
	private Date scheduled_at;

	public Date getScheduled_at() {
		return scheduled_at;
	}

	public void setScheduled_at(Date scheduled_at) {
		this.scheduled_at = scheduled_at;
	}

	public Offer getOffer_id() {
		return offer_id;
	}

	public void setOffer_id(Offer offer_id) {
		this.offer_id = offer_id;
	}

	public User getUser_id() {
		return user_id;
	}

	public void setUser_id(User user_id) {
		this.user_id = user_id;
	}
}