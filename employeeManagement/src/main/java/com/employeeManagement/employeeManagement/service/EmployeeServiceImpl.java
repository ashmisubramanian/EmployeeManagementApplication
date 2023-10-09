package com.employeeManagement.employeeManagement.service;

import com.employeeManagement.employeeManagement.DTO.EmployeeResponse;
import com.employeeManagement.employeeManagement.enumModel.Role;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.model.Project;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Employee updateEmployee(Employee updateEmployee,Employee employee,String role) {
        employee.setFirstName(updateEmployee.getFirstName());
        employee.setLastName(updateEmployee.getLastName());
        employee.setEmail(updateEmployee.getEmail());
        employee.setPassword(passwordEncoder.encode(updateEmployee.getPassword()));
        if(role.equals("USER") && updateEmployee.getRole()!=null){
            return null;
        }
        if(updateEmployee.getRole()==null){
            employee.setRole(employee.getRole());
            return employee;
        }
        employee.setRole(updateEmployee.getRole());
        return employee;
    }

    @Override
    public List<Long> deleteEmployee(Long id) {
        Employee employeeFounded=employeeRepository.getReferenceById(id);
        List<Project> employeeProjects = projectRepository.findByEmployee(employeeFounded);
        List<Project> managerProjects = projectRepository.findByManagerId(employeeFounded);
        employeeProjects.addAll(managerProjects);
        List<Project> projects = employeeProjects;
        List<Long> projectIds = projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
        return projectIds;
    }

    @Override
    public List<EmployeeResponse> getEmployees() {
        List<Employee> employees=employeeRepository.findAll();
        List<EmployeeResponse> employeeResponses=new ArrayList<>();
        for (Employee employee: employees) {
            EmployeeResponse employeeResponse=new EmployeeResponse();
            employeeResponse.setId(employee.getId());
            employeeResponse.setFirstName(employee.getFirstName());
            employeeResponse.setLastName(employee.getLastName());
            employeeResponse.setEmail(employee.getEmail());
            employeeResponse.setRole(employee.getRole());
            List<String> projectTitles = employee.getProject().stream()
                    .map(Project::getTitle)
                    .collect(Collectors.toList());

            employeeResponse.setProjectTitles(projectTitles);

            employeeResponses.add(employeeResponse);
        }
        return employeeResponses;
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Optional<Employee> employee=employeeRepository.findById(id);
        EmployeeResponse employeeResponse=new EmployeeResponse();
        if(employee.isPresent()){
            employeeResponse.setId(employee.get().getId());
            employeeResponse.setFirstName(employee.get().getFirstName());
            employeeResponse.setLastName(employee.get().getLastName());
            employeeResponse.setEmail(employee.get().getEmail());
            employeeResponse.setRole(employee.get().getRole());
            List<String> projectTitles = employee.get().getProject().stream()
                    .map(Project::getTitle)
                    .collect(Collectors.toList());

            employeeResponse.setProjectTitles(projectTitles);
        }
        return employeeResponse;
    }

    @Override
    public ResponseEntity<EmployeeResponse> saveEmployee(Employee saveEmployee) {
        saveEmployee.setPassword(passwordEncoder.encode(saveEmployee.getPassword()));
        saveEmployee.setRole(Role.USER);
        Employee employee= employeeRepository.save(saveEmployee);
        return ResponseEntity.ok(getEmployeeById(employee.getId()));
    }
}
