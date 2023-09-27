package com.employeeManagement.employeeManagement.service;

import com.employeeManagement.employeeManagement.DTO.EmployeeResponse;
import com.employeeManagement.employeeManagement.DTO.ProjectResponse;
import com.employeeManagement.employeeManagement.model.Employee;

import java.util.List;

public interface EmployeeService {
    public Employee updateEmployee(Employee updateEmployee,Employee employee,String role);
    public List<Long> deleteEmployee(Long id);
    public List<EmployeeResponse> getEmployees();

    public EmployeeResponse getEmployeeById(Long id);
}
