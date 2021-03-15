package com.campaign_management.campaign_management.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.campaign_management.campaign_management.model.Offer;
import com.campaign_management.campaign_management.repository.OfferRepository;
import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.model.User;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/offer")
public class OfferController {

	@Autowired
	private OfferRepository offerRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(method=RequestMethod.POST, value="/{id}")
	public ResponseEntity<?> addOffer(@RequestBody Offer offer,@PathVariable int id){
		
		Optional<User> user = userRepository.findById(id);
		
		if( user.isPresent() ) {
			
			if( offer.getData() != null ) {
				if( offer.getCreated_at() != null ) {
					offer.setUser_id(user.get());
					offerRepository.save(offer);
					return new ResponseEntity<Offer>(offer, HttpStatus.OK);
				}
				else
					return new ResponseEntity<>("created_at should not be null", HttpStatus.NOT_ACCEPTABLE);
			}
			else
				return new ResponseEntity<>("data should not be null", HttpStatus.NOT_ACCEPTABLE);
		}
		else 
			return new ResponseEntity<>("No users available for the requested user_id", HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping("/")
	public ResponseEntity<?> getOffer(){
		
		List<Offer> offers = offerRepository.findAll();
		
		if( offers.size() > 0 ) {
			return new ResponseEntity<List<Offer>>(offers, HttpStatus.OK);
		}
		else
			return new ResponseEntity<>("No offers available", HttpStatus.NOT_FOUND);	
	}
}
