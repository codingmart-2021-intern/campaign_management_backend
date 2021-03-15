package com.campaign_management.campaign_management.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.campaign_management.campaign_management.model.Schedule;

@Repository
public interface ScheduleRepository  extends JpaRepository<Schedule, Integer> {

	@Query(value="SELECT * FROM schedule WHERE scheduled_at=:scheduled_at", nativeQuery = true)
	List<Schedule> findByScheduled_at(Date scheduled_at);
}
