package com.employeeManagement.employeeManagement.service;

import com.employeeManagement.employeeManagement.DTO.ProjectRequest;
import com.employeeManagement.employeeManagement.DTO.ProjectResponse;
import com.employeeManagement.employeeManagement.model.Project;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectService {
    public Project createProject(ProjectRequest projectRequest);

    public List<ProjectResponse> getProjects();

    public ProjectResponse getProjectById(Long id);

    public Project updateProject(ProjectRequest projectRequest,Project project);

    public ResponseEntity<String> deleteProjectById(Long id);

    public ResponseEntity<String> deleteEmployeeFromProjects(Long id);

}
