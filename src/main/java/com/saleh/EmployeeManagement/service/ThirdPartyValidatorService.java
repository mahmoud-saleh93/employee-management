package com.saleh.EmployeeManagement.service;

import com.saleh.EmployeeManagement.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Service
public class ThirdPartyValidatorService {
    private static final String VALIDATION_SERVICE_URL = "http://localhost:8081/validate";

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private RestTemplate restTemplate;
    @RateLimiter(name = "validationService")
    @CircuitBreaker(name = "emailService", fallbackMethod = "emailFallback")
    public boolean validateEmail(String email) {
        String url = VALIDATION_SERVICE_URL + "/email/" + email;
        try {
            return restTemplate.getForObject(url, Boolean.class);
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException ex) {
            throw new ExternalServiceException("Error occurred while validating email: " + ex.getMessage(), ex);
        }
    }
    @RateLimiter(name = "validationService")
    @CircuitBreaker(name = "emailService", fallbackMethod = "departmentFallback")
    public boolean validateDepartment(String department) {
        String url = VALIDATION_SERVICE_URL + "/department/" + department;
        try {
            return restTemplate.getForObject(url, Boolean.class);
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException ex) {
            throw new ExternalServiceException("Error occurred while validating department: " + ex.getMessage(), ex);
        }
    }

    private boolean emailFallback(String email, Throwable throwable) {
        logger.info("Fallback for email validation due to: " + throwable.getMessage());
        return false;
    }

    private boolean departmentFallback(String department, Throwable throwable) {
        logger.info("Fallback for department validation due to: " + throwable.getMessage());
        return false;
    }
}
