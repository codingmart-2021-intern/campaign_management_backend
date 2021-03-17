package com.campaign_management.campaign_management.controller;

import java.util.List;
import java.util.Optional;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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
import com.campaign_management.campaign_management.repository.ScheduleRepository;
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
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@RequestMapping("/")
	public ResponseEntity<?> getOffer() throws Exception  {
		
		List<Offer> offers = offerRepository.findAll();
		
		if( offers.size() > 0 ) {
			return new ResponseEntity<List<Offer>>(offers, HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(returnJsonString(false,"No offers available"), HttpStatus.NOT_FOUND);	
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/{id}")
	public ResponseEntity<?> addOffer(@RequestBody Offer offer,@PathVariable int id) throws Exception {
		
		try {

			Optional<User> user = userRepository.findById(id);
			
			if( user.isPresent() ) {
				
				if( offer.getData() != null && offer.getTitle() != null ) {
					if( offer.getCreated_at() != null ) {
						offer.setUser_id(user.get());
						offerRepository.save(offer);
						return new ResponseEntity<Offer>(offer, HttpStatus.OK);
					}
					else
						return new ResponseEntity<>(returnJsonString(false,"created_at should not be null"), HttpStatus.NOT_ACCEPTABLE);
				}
				else
					return new ResponseEntity<>(returnJsonString(false,"data should not be null"), HttpStatus.NOT_ACCEPTABLE);
			}
			else 
				return new ResponseEntity<>(returnJsonString(false,"No users available for the requested user_id"), HttpStatus.NOT_FOUND);
			
		}catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/")
	public ResponseEntity<?> updateOffer(@RequestBody Offer offer) throws Exception {

		try {
			Optional<Offer> isOfferPresent = offerRepository.findById(offer.getOffer_id());
			
			if( !isOfferPresent.isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"No offer available for the requested offer_id"), HttpStatus.NOT_FOUND);
			
			Offer offerFetched = isOfferPresent.get();
			
			if( offerFetched.getStatus().equalsIgnoreCase("sent") )
				return new ResponseEntity<>(returnJsonString(false,"Offer cannot be update because it is already sent"), HttpStatus.NOT_FOUND);
			
			if( offer.getCreated_at() == null )
				offer.setCreated_at(offerFetched.getCreated_at());
			
			if( offer.getData() == null )
				offer.setData(offerFetched.getData());
			
			if( offer.getStatus() == null )
				offer.setStatus(offerFetched.getStatus());
			
			if( offer.getTitle() == null )
				offer.setTitle(offerFetched.getTitle());
			
			if( offer.getUser_id() == null )
				offer.setUser_id(offerFetched.getUser_id());
				
			offerRepository.save(offer);
			return new ResponseEntity<Offer>(offer, HttpStatus.OK);
			
		}catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public ResponseEntity<?> deleteOffer(@PathVariable int id) throws Exception {
		try {
			
			Optional<Offer> isOfferPresent = offerRepository.findById(id);
			if( !isOfferPresent.isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"No data available for that id to delete offer"), HttpStatus.NOT_ACCEPTABLE);
			
			if( scheduleRepository.findOneScheduleOfferId(id).isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"It is already scheduled so deletion is not possible"), HttpStatus.NOT_ACCEPTABLE);
			
			offerRepository.deleteById(id);
			
			return new ResponseEntity<>(returnJsonString(false,"Deleted offer with id = "+id), HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String returnJsonString(boolean status, String response) throws JSONException  {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", response);
        return jsonObject.toString();
	}
}
