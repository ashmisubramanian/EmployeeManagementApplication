package com.employeeManagement.employeeManagement.controllerTest;

import com.employeeManagement.employeeManagement.DTO.EmployeeResponse;
import com.employeeManagement.employeeManagement.controller.EmployeeController;
import com.employeeManagement.employeeManagement.enumModel.Role;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.service.EmployeeService;
import com.employeeManagement.employeeManagement.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;
    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        //employeeService = new EmployeeServiceImpl();
    }


    @Test
    public void testGetEmployeeById() {
        Long id = 1L;
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(id);
        employeeResponse.setFirstName("John");
        employeeResponse.setLastName("Doe");
        employeeResponse.setEmail("john@example.com");
        employeeResponse.setRole(Role.USER);

        when(employeeService.getEmployeeById(id)).thenReturn(employeeResponse);

        ResponseEntity<EmployeeResponse> responseEntity = employeeController.findEmployeeById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        EmployeeResponse responseDto = responseEntity.getBody();
        assertNotNull(responseDto);
        assertEquals(id, responseDto.getId());
        assertEquals("John", responseDto.getFirstName());
        assertEquals("Doe", responseDto.getLastName());
        assertEquals("john@example.com", responseDto.getEmail());
        assertEquals(Role.USER, responseDto.getRole());
    }

    @Test
    public void testGetEmployee() {
        Long id = 1L;
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(id);
        employeeResponse.setFirstName("John");
        employeeResponse.setLastName("Doe");
        employeeResponse.setEmail("john@example.com");
        employeeResponse.setRole(Role.USER);

        List<EmployeeResponse> employeeResponseList = new ArrayList<>();
        employeeResponseList.add(employeeResponse);

        when(employeeService.getEmployees()).thenReturn(employeeResponseList);

        List<EmployeeResponse> responseList = employeeController.getEmployees();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        EmployeeResponse responseDto = responseList.get(0);
        assertEquals(id, responseDto.getId());
        assertEquals("John", responseDto.getFirstName());
        assertEquals("Doe", responseDto.getLastName());
        assertEquals("john@example.com", responseDto.getEmail());
        assertEquals(Role.USER, responseDto.getRole());
    }

    @Test
    public void testSaveEmployee() {
        // Mock data
        Employee saveEmployee = new Employee();
        saveEmployee.setEmail("test@example.com");
        saveEmployee.setPassword("password123");

        EmployeeResponse savedEmployeeResponse = new EmployeeResponse(1L, "John", "Doe", "test@example.com", Role.USER);

        when(employeeService.saveEmployee(saveEmployee)).thenReturn(ResponseEntity.ok(savedEmployeeResponse));

        ResponseEntity<EmployeeResponse> responseEntity = employeeController.saveEmployee(saveEmployee);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity.getBody());
        assertEquals(1L, responseEntity.getBody().getId());

        verify(employeeService, times(1)).saveEmployee(saveEmployee);
    }


    @Test
    public void testUpdateEmployeeWithUserRole() {
        Employee updateEmployee = new Employee();
        updateEmployee.setFirstName("John");
        updateEmployee.setLastName("Doe");
        updateEmployee.setEmail("john@example.com");
        updateEmployee.setPassword("newPassword123");
        updateEmployee.setRole(Role.USER);

        Employee existingEmployee = new Employee();
        existingEmployee.setRole(Role.USER);

        Employee result = employeeService.updateEmployee(updateEmployee, existingEmployee, "USER");

        assertNull(result);
    }

    @Test
    public void testUpdateEmployeeWithAdminRole() {
        Long id = 1L;
        Employee updateEmployee = new Employee();
        updateEmployee.setFirstName("John");
        updateEmployee.setLastName("Doe");
        updateEmployee.setEmail("john@example.com");
        updateEmployee.setPassword("Password123");
        updateEmployee.setRole(Role.ADMIN);

        Employee existingEmployee = new Employee();

        when(employeeRepository.findById(eq(id))).thenReturn(Optional.of(existingEmployee));

        when(employeeService.updateEmployee(updateEmployee, existingEmployee, "ADMIN")).thenReturn(updateEmployee);

        Authentication authentication = new UsernamePasswordAuthenticationToken("adminUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<String> response = employeeController.updateEmployee(id, updateEmployee);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee with id " + id + " updated successfully", response.getBody());
    }




    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }
    }





}
