package com.campaign_management.campaign_management.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campaign_management.campaign_management.model.Schedule;
import com.campaign_management.campaign_management.repository.ScheduleRepository;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepository;
	
	public List<Schedule> getOneSchedule(Date date){
		
		List<Schedule> schedule = new ArrayList<>();
		scheduleRepository.findByScheduled_at(date).forEach(schedule::add);
		return schedule;
	}
}
