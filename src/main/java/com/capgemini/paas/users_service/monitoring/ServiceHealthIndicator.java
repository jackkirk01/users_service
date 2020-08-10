package com.capgemini.paas.users_service.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.capgemini.paas.users_service.persistence.dao.UserRepository;

@Component
public class ServiceHealthIndicator implements HealthIndicator {
	
	@Autowired
	UserRepository userRepository;
	
	@Override
    public Health health() {
		
		boolean isDown = false;
        
		try {
    		if (userRepository.findAll() == null) {
    			isDown = true;
    		} else {
    			isDown = false;
    		}
    	} catch (Exception ex) {
    		isDown = true;
    	}
		
        return isDown ? Health.down().withDetail("ERROR", "Database cannot be detected").build() : Health.up().build();
        
    }
 
}