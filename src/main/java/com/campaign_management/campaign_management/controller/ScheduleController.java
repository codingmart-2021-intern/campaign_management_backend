package com.campaign_management.campaign_management.controller;

import java.util.Date;
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

import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.service.ScheduleService;
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
	
	@Autowired
	private ScheduleService scheduleService;
	
	@RequestMapping(method=RequestMethod.POST, value="/{user_id}/{offer_id}")
	public ResponseEntity<?> addSchedule(@RequestBody Schedule schedule, @PathVariable int user_id, @PathVariable int offer_id){
				
		Optional<User> user = userRepository.findById(user_id);
		Optional<Offer> offer = offerRepository.findById(offer_id);
		
		if( user.isPresent() ) {
			
			if( offer.isPresent() ) {
				
				Date scheduleDate = schedule.getScheduled_at();
				System.out.println(scheduleService.getOneSchedule(scheduleDate));
				
				if( scheduleDate != null && scheduleService.getOneSchedule(scheduleDate).size() == 0 ) {
					
					schedule.setOffer_id(offer.get());
					schedule.setUser_id(user.get());
					scheduleRepository.save(schedule);
					return new ResponseEntity<Schedule> (schedule, HttpStatus.OK);
				}
				else
					return new ResponseEntity<>("schedule_at should not be null", HttpStatus.NOT_ACCEPTABLE);
			}
			else
				return new ResponseEntity<>("No offers found with that offer_id", HttpStatus.NOT_FOUND);
		}
		else
			return new ResponseEntity<>("No users found with that user_id", HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping("/")
	public ResponseEntity<?> getSchedule(){
		
		List<Schedule> schedule = scheduleRepository.findAll();
		
		if( schedule.size() > 0 )
			return new ResponseEntity<List<Schedule>> (schedule, HttpStatus.OK);
		else
			return new ResponseEntity<>("No schedules found", HttpStatus.NOT_FOUND);
	}
}
