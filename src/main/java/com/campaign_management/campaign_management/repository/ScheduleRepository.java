package com.campaign_management.campaign_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.campaign_management.campaign_management.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

	@Query(value = "SELECT * FROM schedule WHERE offer_id=:offer_id", nativeQuery = true)
	Optional<List<Schedule>> findOneScheduleOfferId(int offer_id); 
}
