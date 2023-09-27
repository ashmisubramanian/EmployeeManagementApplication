package com.employeeManagement.employeeManagement.controller;

import com.employeeManagement.employeeManagement.DTO.ProjectRequest;
import com.employeeManagement.employeeManagement.DTO.ProjectResponse;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.model.Project;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import com.employeeManagement.employeeManagement.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public @ResponseBody List<ProjectResponse> getEmployees(){
        return projectService.getProjects();
    }

    @GetMapping(params = "id")
    public @ResponseBody ResponseEntity<ProjectResponse> findEmployeeById(@RequestParam(name = "id") Long id){
        ProjectResponse projectResponse=projectService.getProjectById(id);
        if(projectResponse!=null){
            return ResponseEntity.ok(projectResponse);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(projectResponse);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        Project savedProject= projectService.createProject(projectRequest);
        Long id= savedProject.getId();
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id,@Valid @RequestBody ProjectRequest projectRequest){
        Optional<Project> projectPresent= projectRepository.findById(id);
        if(projectPresent.isPresent()){
            Project project=projectPresent.get();
            projectRepository.saveAndFlush(projectService.updateProject(projectRequest,project));
            return ResponseEntity.ok("Project with id "+id+" updated successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project with ID " + id + " does not exist.");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){
        Optional<Project> project= projectRepository.findById(id);
        if(project.isPresent()){
            projectRepository.deleteById(id);
            return ResponseEntity.ok("Project with Id{"+id+"} deleted");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Project with Id {"+id+"} doesn't exist");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/removeEmployeeFromProjects/{id}")
    public ResponseEntity<String> deleteEmployeeFromProject(@PathVariable Long id){
        Employee employeeFounded=employeeRepository.getReferenceById(id);
        List<Project> projects=projectRepository.findByEmployee(employeeFounded);
        if (!projects.isEmpty()) {
            for (Project project:projects) {
                List<Employee> employees = project.getEmployee();
                Employee employee = employees.stream()
                        .filter(e -> e.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (employee != null) {
                    employees.remove(employee);
                    project.setEmployee(employees);
                    projectRepository.saveAndFlush(project);
                }
            }
            return ResponseEntity.ok("Employee removed from projects.");
        }
        else {
            return ResponseEntity.ok("Employee is not present in any projects.");
        }
    }


}
