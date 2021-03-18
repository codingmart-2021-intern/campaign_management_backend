package com.campaign_management.campaign_management.controller;

import java.util.Date;
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
import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.model.Offer;
import com.campaign_management.campaign_management.model.Schedule;
import com.campaign_management.campaign_management.repository.OfferRepository;
import com.campaign_management.campaign_management.repository.ScheduleRepository;
import com.campaign_management.campaign_management.model.User;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OfferRepository offerRepository;
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@RequestMapping("/")
	public ResponseEntity<?> getSchedule() throws Exception  {
		
		List<Schedule> schedule = scheduleRepository.findAll();
		
		if( schedule.size() > 0 )
			return new ResponseEntity<List<Schedule>> (schedule, HttpStatus.OK);
		else
			return new ResponseEntity<>(returnJsonString(false,"No schedules found"), HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/{user_id}/{offer_id}")
	public ResponseEntity<?> addSchedule(@RequestBody Schedule schedule, @PathVariable int user_id, @PathVariable int offer_id) throws Exception  {
		
		try {
			Optional<User> user = userRepository.findById(user_id);
			Optional<Offer> offer = offerRepository.findById(offer_id);
			
			if( user.isPresent() ) {
				
				if( offer.isPresent() ) {
					
					if( schedule.getScheduled_at() != null ) {
						
						schedule.setOffer_id(offer.get());
						schedule.setUser_id(user.get());
						scheduleRepository.save(schedule);
						return new ResponseEntity<Schedule> (schedule, HttpStatus.OK);
					}
					else
						return new ResponseEntity<>(returnJsonString(false,"schedule_at should not be null"), HttpStatus.NOT_ACCEPTABLE);
				}
				else
					return new ResponseEntity<>(returnJsonString(false,"No offers found with that offer_id"), HttpStatus.OK);
			}
			else
				return new ResponseEntity<>(returnJsonString(false,"No users found with that user_id"), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/update/{user_id}/{offer_id}/{id}")
	public ResponseEntity<?> updateSchedule(@RequestBody Schedule schedule, @PathVariable int id, @PathVariable int user_id, @PathVariable int offer_id) throws Exception  {

		try {
			
			Optional<User> user = userRepository.findById(user_id);
			Optional<Offer> offer = offerRepository.findById(offer_id);
			
			if( user.isPresent() ) {
				
				if( offer.isPresent() ) {
					
					Date scheduleDate = schedule.getScheduled_at();
					
					if( scheduleDate == null )
						return new ResponseEntity<>(returnJsonString(false,"schedule_at should not be null"), HttpStatus.NOT_ACCEPTABLE);
					
					if( offerRepository.findById(offer_id).get().getStatus() == "sent" )
						return new ResponseEntity<>(returnJsonString(false,"Updating schedule is not possible because it is already scheduled"), HttpStatus.NOT_ACCEPTABLE);
					
					Optional<Schedule> isSchedulePresent = scheduleRepository.findById(id);
					
					if( !isSchedulePresent.isPresent() )
						return new ResponseEntity<>(returnJsonString(false,"Schedule is not available"), HttpStatus.NOT_ACCEPTABLE);
					
					Schedule scheduleFetched = isSchedulePresent.get();
					
					if( schedule.getUser_id() == null )
						schedule.setUser_id(scheduleFetched.getUser_id());
					if( schedule.getOffer_id() == null )
						schedule.setOffer_id(scheduleFetched.getOffer_id());
					
					schedule.setSchedule_id(id);
					scheduleRepository.save(schedule);
					
					return new ResponseEntity<Schedule> (schedule, HttpStatus.OK);
				}
				else
					return new ResponseEntity<>(returnJsonString(false,"No offers found with that offer_id"), HttpStatus.NOT_FOUND);
			}
			else
				return new ResponseEntity<>(returnJsonString(false,"No schedules found"), HttpStatus.OK);
		} catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(returnJsonString(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/delete/{user_id}/{offer_id}/{id}")
	public ResponseEntity<?> deleteSchedule(@PathVariable int id, @PathVariable int user_id, @PathVariable int offer_id) throws Exception  {
		
		try {
			
			
			if( offerRepository.findById(offer_id).get().getStatus() == "sent" )
				return new ResponseEntity<>(returnJsonString(false,"Deleting schedule is not possible because it is already scheduled"), HttpStatus.NOT_ACCEPTABLE);
			
			Optional<Schedule> isSchedulePresent = scheduleRepository.findById(id);
			
			if( !isSchedulePresent.isPresent() )
				return new ResponseEntity<>(returnJsonString(false,"Schedule is not available to delete"), HttpStatus.NOT_ACCEPTABLE);
			
			Schedule schedule = isSchedulePresent.get();
			
			scheduleRepository.deleteById(id);
			return new ResponseEntity<Schedule> (schedule, HttpStatus.OK);
			
		} catch (Exception e) {
            e.printStackTrace();
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
