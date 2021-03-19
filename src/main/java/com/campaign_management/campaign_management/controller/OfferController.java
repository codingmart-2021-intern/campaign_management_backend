package com.campaign_management.campaign_management.controller;

import java.util.ArrayList;
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
import com.campaign_management.campaign_management.model.Schedule;
import com.campaign_management.campaign_management.repository.OfferRepository;
import com.campaign_management.campaign_management.model.Schedule;
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

//	Get all offers
	@RequestMapping("/")
	public ResponseEntity<?> getOffer() throws Exception  {
		
		List<Offer> offers = offerRepository.findAll();
		System.out.println("Offers all");
		if( offers.size() > 0 ) {
			return new ResponseEntity<List<Offer>>(offers, HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(returnJsonString(false,"No offers available"), HttpStatus.OK);	
	}
	
//	Get Non scheduled offers
	@RequestMapping("/notScheduledOffer")
	public ResponseEntity<?> getNonScheduledOffer() throws Exception {
		try {

			Optional<List<Offer>> nonScheduledOffers = offerRepository.findNonSchduledOffers();
			
			if( nonScheduledOffers.isPresent() ) {
				List<JSONObject> jsonObject = new ArrayList<JSONObject>();
				
				for(Offer nonSchedules: nonScheduledOffers.get()) {
					
					JSONObject jsonObjectOffer = new JSONObject();
					JSONObject jsonObjectUser = new JSONObject();
					
					jsonObjectOffer.put("offer_id",nonSchedules.getOffer_id());
					jsonObjectOffer.put("created_at",nonSchedules.getCreated_at());
					jsonObjectOffer.put("status",nonSchedules.getStatus());
					jsonObjectOffer.put("title",nonSchedules.getTitle());
					jsonObjectOffer.put("data",nonSchedules.getData());
					jsonObjectOffer.put("schedule_at","");
					jsonObjectUser.put("id",nonSchedules.getUser_id().getId());
					jsonObjectUser.put("dob",nonSchedules.getUser_id().getDOB());
					jsonObjectUser.put("email",nonSchedules.getUser_id().getEmail());
					jsonObjectUser.put("image", nonSchedules.getUser_id().getImage());
					jsonObjectUser.put("name", nonSchedules.getUser_id().getName());
					jsonObjectUser.put("phone", nonSchedules.getUser_id().getPhone());
					jsonObjectUser.put("password", nonSchedules.getUser_id().getPassword());
					jsonObjectUser.put("role",nonSchedules.getUser_id().getRole());
					jsonObjectUser.put("enabled", nonSchedules.getUser_id().getEnabled());
					jsonObjectUser.put("verificationCode",nonSchedules.getUser_id().getVerificationCode());
					jsonObjectUser.put("mbverify", nonSchedules.getUser_id().getMbVerify());
					jsonObjectUser.put("gender", nonSchedules.getUser_id().getGender());
					jsonObjectUser.put("otp", nonSchedules.getUser_id().getOtp());
					jsonObjectUser.put("timestamp", nonSchedules.getUser_id().getTimestamp());
					jsonObjectOffer.put("user_id",jsonObjectUser);
					jsonObject.add(jsonObjectOffer);
				}
				return new ResponseEntity<> (jsonObject.toString(), HttpStatus.OK);
			}
			else
				return new ResponseEntity<>(returnJsonString(false,"No offers available"), HttpStatus.OK);
				
		}catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	Add an offer
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
					return new ResponseEntity<>(returnJsonString(false,"data and title should not be null"), HttpStatus.NOT_ACCEPTABLE);
			}
			else 
				return new ResponseEntity<>(returnJsonString(false,"No users available for the requested user_id"), HttpStatus.OK);
			
		}catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	Update an offer
	@RequestMapping(method=RequestMethod.PUT, value="/{id}")
	public ResponseEntity<?> updateOffer(@RequestBody Offer offer,@PathVariable int id) throws Exception {

		try {
			Optional<Offer> isOfferPresent = offerRepository.findById(id);
			
			if( !isOfferPresent.isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"No offer available for the requested offer_id"), HttpStatus.OK);
			
			Offer offerFetched = isOfferPresent.get();
			
			if( offerFetched.getStatus().equalsIgnoreCase("sent") )
				return new ResponseEntity<>(returnJsonString(false,"Offer cannot be update because it is already sent"), HttpStatus.OK);
			
			
			offer.setOffer_id(offerFetched.getOffer_id());
			
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
			
			if( offer.getTitle() == null )
				offer.setTitle(offerFetched.getTitle());
				
			offerRepository.save(offer);
			return new ResponseEntity<Offer>(offer, HttpStatus.OK);
			
		}catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	Delete an offer
	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public ResponseEntity<?> deleteOffer(@PathVariable int id) throws Exception {
		try {
			
			Optional<Offer> isOfferPresent = offerRepository.findById(id);
			if( !isOfferPresent.isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"No data available for that id to delete offer"), HttpStatus.NOT_ACCEPTABLE);
			
			Optional<Schedule> isSchedulePresent = scheduleRepository.findById(id);
			if( isSchedulePresent.isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"It is already scheduled so deletion is not possible"), HttpStatus.NOT_ACCEPTABLE);
			
			offerRepository.deleteById(id);
			
			return new ResponseEntity<>(returnJsonString(true,"Deleted offer with id = "+id), HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	JSON return message
	public String returnJsonString(boolean status, String response) throws JSONException  {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", response);
        return jsonObject.toString();
	}
}
