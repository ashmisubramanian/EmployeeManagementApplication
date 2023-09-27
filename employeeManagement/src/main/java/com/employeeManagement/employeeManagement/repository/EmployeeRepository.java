package com.employeeManagement.employeeManagement.repository;

import com.employeeManagement.employeeManagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    //Employee findByEmail(String email);
    Optional<Employee> findByEmail(String email);
}