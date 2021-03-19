package com.campaign_management.campaign_management.controller;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.campaign_management.campaign_management.model.Customer;
import com.campaign_management.campaign_management.repository.CustomerRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;
	
//	Get all customers detail
	@RequestMapping("/")
	public ResponseEntity<?> getCustomers() throws Exception {
		
		List<Customer> customers = customerRepository.findAll();
		
		if( customers.size() > 0 )
			return new ResponseEntity<List<Customer>> (customers,HttpStatus.OK);
		else
			return new ResponseEntity<>(returnJsonString(false,"No customers available"), HttpStatus.OK);
	}
	
//	Add customers details
	@RequestMapping(method=RequestMethod.POST,value="/upload")
	public ResponseEntity<?> addCustomers(@RequestBody List<Customer> customers) throws Exception {
		
		for(Customer a: customers) {
			System.out.println(a.getCustomer_email());
		}
		
		try {
			
			customerRepository.saveAll(customers);
			return new ResponseEntity<>(returnJsonString(true,"Inserted successfully"), HttpStatus.OK);
		}catch(Exception e) {
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
