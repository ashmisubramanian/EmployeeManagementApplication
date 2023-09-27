package com.employeeManagement.employeeManagement.service;

import com.employeeManagement.employeeManagement.DTO.ProjectEmployeeResponse;
import com.employeeManagement.employeeManagement.DTO.ProjectRequest;
import com.employeeManagement.employeeManagement.DTO.ProjectResponse;
import com.employeeManagement.employeeManagement.enumModel.ProjectStatus;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.model.Project;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project createProject(ProjectRequest projectRequest) {
        Project project = new Project();
        project.setTitle(projectRequest.getTitle());

        // Find and set the manager using managerId from the request
        Employee manager = employeeRepository.findById(projectRequest.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + projectRequest.getManagerId()));
        project.setManagerId(manager);


        List<Long> employeeIds = projectRequest.getEmployeeIds();

        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        List<Long> missingEmployeeIds = employeeIds.stream()
                .filter(id -> employees.stream().noneMatch(employee -> employee.getId().equals(id)))
                .collect(Collectors.toList());

        if (!missingEmployeeIds.isEmpty()) {
            throw new ResourceNotFoundException("Employee(s) with IDs " + missingEmployeeIds + " not found");
        }

        project.setEmployee(employees);

        project.setStatus(ProjectStatus.TODO);

        Project savedProject = projectRepository.save(project);
        return savedProject;
    }

    @Override
    public List<ProjectResponse> getProjects() {
        List<Project> projects=projectRepository.findAll();
        List<ProjectResponse> projectResponses=new ArrayList<>();

        for (Project project: projects) {
            ProjectResponse projectResponse=new ProjectResponse();
            projectResponse.setId(project.getId());
            projectResponse.setTitle(project.getTitle());
            projectResponse.setManagerId(mapToProjectEmployeeResponse(project.getManagerId()));
            List<ProjectEmployeeResponse> employeeResponses = project.getEmployee().stream()
                    .map(this::mapToProjectEmployeeResponse)
                    .collect(Collectors.toList());
            projectResponse.setEmployees(employeeResponses);
            projectResponse.setStatus(project.getStatus());

            projectResponses.add(projectResponse);
        }
        return projectResponses;
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Optional<Project> project=projectRepository.findById(id);
        ProjectResponse projectResponse=new ProjectResponse();
        if (project.isPresent()){
            projectResponse.setId(project.get().getId());
            projectResponse.setTitle(project.get().getTitle());
            projectResponse.setManagerId(mapToProjectEmployeeResponse(project.get().getManagerId()));
            List<ProjectEmployeeResponse> employeeResponses = project.get().getEmployee().stream()
                    .map(this::mapToProjectEmployeeResponse)
                    .collect(Collectors.toList());
            projectResponse.setEmployees(employeeResponses);
            projectResponse.setStatus(project.get().getStatus());

        }
        return projectResponse;
    }

    @Override
    public Project updateProject(ProjectRequest projectRequest, Project project) {
        project.setTitle(projectRequest.getTitle());
        Employee manager = employeeRepository.findById(projectRequest.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + projectRequest.getManagerId()));
        project.setManagerId(manager);
        List<Long> employeeIds = projectRequest.getEmployeeIds();

        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        List<Long> missingEmployeeIds = employeeIds.stream()
                .filter(id -> employees.stream().noneMatch(employee -> employee.getId().equals(id)))
                .collect(Collectors.toList());

        if (!missingEmployeeIds.isEmpty()) {
            throw new ResourceNotFoundException("Employee(s) with IDs " + missingEmployeeIds + " not found");
        }

        project.setEmployee(employees);
        project.setStatus(projectRequest.getStatus());
        return project;
    }


    private ProjectEmployeeResponse mapToProjectEmployeeResponse(Employee employee) {
        ProjectEmployeeResponse projectEmployeeResponse = new ProjectEmployeeResponse();
        projectEmployeeResponse.setId(employee.getId());
        projectEmployeeResponse.setFirstName(employee.getFirstName());
        projectEmployeeResponse.setLastName(employee.getLastName());
        return projectEmployeeResponse;
    }
}
