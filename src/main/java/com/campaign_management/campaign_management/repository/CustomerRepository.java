package com.campaign_management.campaign_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campaign_management.campaign_management.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
