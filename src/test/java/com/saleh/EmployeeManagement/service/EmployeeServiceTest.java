package com.saleh.EmployeeManagement.service;

import com.saleh.EmployeeManagement.exception.EmployeeNotFoundException;
import com.saleh.EmployeeManagement.model.Employee;
import com.saleh.EmployeeManagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {
    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceTest.class);
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @Mock
    private ThirdPartyValidatorService validatorService;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("mahmoud");
        employee.setLastName("saleh");
        employee.setDepartment("Sales");
        employee.setEmail("mahmoud0saleh94@gmail.com");
        employee.setSalary(63000);
    }

    @Test
    public void testCreateEmployee_Success() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(validatorService.validateEmail("mahmoud0saleh94@gmail.com")).thenReturn(true);
        when(validatorService.validateDepartment("Sales")).thenReturn(true);
        //when(notificationService.sendEmployeeCreationNotification(any(Employee.class)));
        Employee savedEmployee = employeeService.createEmployee(employee);
        assertEquals("mahmoud", savedEmployee.getFirstName());
        assertEquals("saleh", savedEmployee.getLastName());
    }

    @Test
    public void testGetEmployeeById_Success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        Employee foundEmployee = employeeService.getEmployee(employee.getId());
        assertNotNull(foundEmployee);
        assertEquals("mahmoud0saleh94@gmail.com", foundEmployee.getEmail());
    }

    @Test
    public void testGetEmployeeById_EmployeeNotFoundException() {
        UUID randomId = UUID.randomUUID();
        when(employeeRepository.findById(randomId)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployee(randomId));
    }

    @Test
    public void testUpdateEmployee_Success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        employee.setLastName("saleh");
        Employee updatedEmployee = employeeService.updateEmployee(employee.getId(), employee);

        assertEquals("saleh", updatedEmployee.getLastName());
    }

    @Test
    public void testDeleteEmployee_Success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        employeeService.deleteEmployee(employee.getId());
        verify(employeeRepository, times(1)).delete(employee);
    }
}
