package com.saleh.EmployeeManagement.service;

import com.saleh.EmployeeManagement.exception.EmployeeNotFoundException;
import com.saleh.EmployeeManagement.exception.InvalidInputException;
import com.saleh.EmployeeManagement.model.Employee;
import com.saleh.EmployeeManagement.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ThirdPartyValidatorService validatorService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public Employee createEmployee(Employee employee) {
        logger.info("Starting employee creation process for: {}", employee);

        if (!validatorService.validateEmail(employee.getEmail())) {
            logger.error("Email validation failed for: {}", employee.getEmail());
            throw new InvalidInputException("Invalid email address");
        }
        if (!validatorService.validateDepartment(employee.getDepartment())) {
            logger.error("Department validation failed for: {}", employee.getDepartment());
            throw new InvalidInputException("Invalid department");
        }
        logger.info("Email and department validated. Proceeding to save employee.");

        Employee savedEmployee = repository.save(employee);

        logger.info("Employee created with ID: {}", savedEmployee.getId());

        notificationService.sendEmployeeCreationNotification(savedEmployee);
        logger.info("Employee creation notification sent for: {}", savedEmployee.getEmail());

        return savedEmployee;    }

    public Employee getEmployee(UUID id) {
        logger.info("the employee data with the following id :" + id + "was requested.");
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
    }

    public Employee updateEmployee(UUID id, Employee updatedEmployee) {
        logger.info("Starting employee update process for employee ID: {}", id);

        try {
            Employee existingEmployee = getEmployee(id);
            logger.info("Found existing employee for ID {}: {}", id, existingEmployee);

            if (updatedEmployee.getFirstName() != null) {
                existingEmployee.setFirstName(updatedEmployee.getFirstName());
            }
            if (updatedEmployee.getLastName() != null) {
                existingEmployee.setLastName(updatedEmployee.getLastName());
            }
            if (updatedEmployee.getEmail() != null) {
                existingEmployee.setEmail(updatedEmployee.getEmail());
            }
            if (updatedEmployee.getDepartment() != null) {
                existingEmployee.setDepartment(updatedEmployee.getDepartment());
            }
            if (updatedEmployee.getSalary() != 0) {
                existingEmployee.setSalary(updatedEmployee.getSalary());
            }

            Employee savedEmployee = repository.save(existingEmployee);
            logger.info("Employee updated successfully for ID: {}", id);

            notificationService.sendEmployeeUpdateNotification(savedEmployee);
            logger.info("Notification sent for employee update.");

            return savedEmployee;

        } catch (EmployeeNotFoundException e) {
            logger.error("No employee found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during the update process for employee ID: {}", id, e);
            throw e;
        }
    }


    public void deleteEmployee(UUID employeeId) {
        Employee employee = repository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        repository.delete(employee);
    }

    public List<Employee> getAllEmployees() {
        logger.info("getting all employees.");
        return repository.findAll();
    }
}
