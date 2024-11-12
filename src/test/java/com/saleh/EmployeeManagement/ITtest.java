package com.saleh.EmployeeManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saleh.EmployeeManagement.model.Employee;
import com.saleh.EmployeeManagement.repository.EmployeeRepository;
import com.saleh.EmployeeManagement.service.NotificationService;
import com.saleh.EmployeeManagement.service.ThirdPartyValidatorService;;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ITtest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @MockBean
    private ThirdPartyValidatorService validatorService;
    @Mock
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();

        employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("mahmoud");
        employee.setLastName("saleh");
        employee.setDepartment("Sales");
        employee.setEmail("mahmoud0saleh94@gmail.com");
        employee.setSalary(63000);
        employee = employeeRepository.save(employee);

        when(validatorService.validateEmail("mahmoud0saleh94@gmail.com")).thenReturn(true);
        when(validatorService.validateDepartment("Sales")).thenReturn(true);
    }

    @Test
    public void testCreateEmployee_Success() throws Exception {
        Employee newEmployee = new Employee();
        newEmployee.setId(UUID.randomUUID());
        newEmployee.setFirstName("mahmoud");
        newEmployee.setLastName("saleh");
        newEmployee.setDepartment("Sales");
        newEmployee.setEmail("mahmoud0saleh94@gmail.com");
        newEmployee.setSalary(63000);
        when(validatorService.validateEmail("mahmoud0saleh94@gmail.com")).thenReturn(true);
        when(validatorService.validateDepartment("Sales")).thenReturn(true);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("mahmoud"))
                .andExpect(jsonPath("$.lastName").value("saleh"));
    }

    @Test
    public void testGetEmployeeById_Success() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mahmoud0saleh94@gmail.com"));
    }

    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateEmployee_Success() throws Exception {
        employee.setLastName("Johnson");

        mockMvc.perform(put("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Johnson"));
    }

    @Test
    public void testDeleteEmployee_Success() throws Exception {
        assertTrue(employeeRepository.findById(employee.getId()).isPresent());

        mockMvc.perform(delete("/api/employees/{id}", employee.getId()))
                .andExpect(status().isNoContent());

        assertFalse(employeeRepository.findById(employee.getId()).isPresent());
    }

    @Test
    public void testListAllEmployees_Success() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("mahmoud"));
    }
}
