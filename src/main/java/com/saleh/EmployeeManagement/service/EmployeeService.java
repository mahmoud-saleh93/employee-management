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

    public Employee updateEmployee(UUID id, Employee employee) {
        Employee existingEmployee =null;
        logger.info("Starting employee update process foremployee id:" + existingEmployee);
        try {
            logger.info("getting existing employee with id:" + id);
            existingEmployee = getEmployee(id);
            logger.info("the existing employee for id:" + id + " is " + employee);
        } catch (EmployeeNotFoundException e){
            logger.error("there is no employee with the following id:"+ id);
        }
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setSalary(employee.getSalary());
        Employee savedEmployee = repository.save(employee);
        logger.info("employee updated.");

        notificationService.sendEmployeeUpdateNotification(existingEmployee);
        logger.info("notification sent for employee update.");

        return savedEmployee;
    }

//    public void deleteEmployee(UUID id) {
//        logger.info("deleting employee with the following id:" + id);
//        repository.deleteById(id);

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
