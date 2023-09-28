package com.employeeManagement.employeeManagement.controller;

import com.employeeManagement.employeeManagement.DTO.ProjectRequest;
import com.employeeManagement.employeeManagement.DTO.ProjectResponse;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.model.Project;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import com.employeeManagement.employeeManagement.service.ProjectService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Project Management")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectService projectService;


    @Operation(
            description = "GET endpoint for Project",
            summary = "GET endpoint to retrieve all projects information",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping("/")
    public @ResponseBody List<ProjectResponse> getEmployees(){
        return projectService.getProjects();
    }


    @Operation(
            description = "GET endpoint for Project",
            summary = "GET endpoint to retrieve a project information by id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
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


    @Operation(
            description = "POST endpoint for Project",
            summary = "POST endpoint to add a project",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Hidden
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        Project savedProject= projectService.createProject(projectRequest);
        Long id= savedProject.getId();
        return ResponseEntity.ok(projectService.getProjectById(id));
    }


    @Operation(
            description = "PUT endpoint for Project",
            summary = "PUT endpoint to update a project",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Hidden
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


    @Operation(
            description = "DELETE endpoint for Project",
            summary = "DELETE endpoint to remove a project",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Hidden
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


    @Operation(
            description = "DELETE endpoint for Project",
            summary = "DELETE endpoint to remove an employee from all projects",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/removeEmployeeFromProjects/{id}")
    @Hidden
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
