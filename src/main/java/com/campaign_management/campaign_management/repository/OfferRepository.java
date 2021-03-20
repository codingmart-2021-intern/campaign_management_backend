package com.campaign_management.campaign_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.campaign_management.campaign_management.model.Offer;


public interface OfferRepository extends JpaRepository<Offer, Integer> {

	@Query(value="SELECT * FROM offer WHERE status IS NULL OR status = 'pending'", nativeQuery = true)
	public Optional<List<Offer>> findNonSchduledOffers();
}
