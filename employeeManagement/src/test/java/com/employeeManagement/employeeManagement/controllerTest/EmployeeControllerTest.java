package com.employeeManagement.employeeManagement.controllerTest;

import com.employeeManagement.employeeManagement.DTO.EmployeeResponse;
import com.employeeManagement.employeeManagement.controller.EmployeeController;
import com.employeeManagement.employeeManagement.enumModel.Role;
import com.employeeManagement.employeeManagement.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEmployeeById() {
        // Prepare a sample employee response
        Long id = 1L;
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(id);
        employeeResponse.setFirstName("John");
        employeeResponse.setLastName("Doe");
        employeeResponse.setEmail("john@example.com");
        employeeResponse.setRole(Role.USER);

        // Mock the behavior of the employeeService
        Mockito.when(employeeService.getEmployeeById(id)).thenReturn(employeeResponse);

        // Act
        ResponseEntity<EmployeeResponse> responseEntity = employeeController.findEmployeeById(id);

        // Assert
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
        // Prepare a sample employee response
        Long id = 1L;
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(id);
        employeeResponse.setFirstName("John");
        employeeResponse.setLastName("Doe");
        employeeResponse.setEmail("john@example.com");
        employeeResponse.setRole(Role.USER);

        List<EmployeeResponse> employeeResponseList = new ArrayList<>();
        employeeResponseList.add(employeeResponse);

        // Mock the behavior of the employeeService
        Mockito.when(employeeService.getEmployees()).thenReturn(employeeResponseList);

        // Act
        List<EmployeeResponse> responseList = employeeController.getEmployees();

        // Assert
        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        EmployeeResponse responseDto = responseList.get(0);
        assertEquals(id, responseDto.getId());
        assertEquals("John", responseDto.getFirstName());
        assertEquals("Doe", responseDto.getLastName());
        assertEquals("john@example.com", responseDto.getEmail());
        assertEquals(Role.USER, responseDto.getRole());
    }


}
