package com.campaign_management.campaign_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campaign_management.campaign_management.model.Offer;


public interface OfferRepository extends JpaRepository<Offer, Integer> {

}
