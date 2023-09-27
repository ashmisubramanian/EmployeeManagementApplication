package com.employeeManagement.employeeManagement.repository;

import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByEmployee(Employee employee);
}
