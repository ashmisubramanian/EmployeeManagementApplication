package com.employeeManagement.employeeManagement.controllerTest;

import com.employeeManagement.employeeManagement.DTO.ProjectEmployeeResponse;
import com.employeeManagement.employeeManagement.DTO.ProjectRequest;
import com.employeeManagement.employeeManagement.DTO.ProjectResponse;
import com.employeeManagement.employeeManagement.controller.ProjectController;
import com.employeeManagement.employeeManagement.enumModel.ProjectStatus;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.model.Project;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import com.employeeManagement.employeeManagement.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;


    @InjectMocks
    private ProjectController projectController;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProject() {
        Long id = 1L;
        ProjectResponse projectResponse = new ProjectResponse();
        ProjectEmployeeResponse employeeResponse=new ProjectEmployeeResponse();
        employeeResponse.setId(id);
        employeeResponse.setFirstName("John");
        employeeResponse.setLastName("Doe");
        projectResponse.setId(id);
        projectResponse.setTitle("Spring Boot");
        projectResponse.setManagerId(employeeResponse);

        List<ProjectResponse> projectResponseList = new ArrayList<>();
        projectResponseList.add(projectResponse);

        when(projectService.getProjects()).thenReturn(projectResponseList);

        List<ProjectResponse> responseList = projectController.getProjects();
        assertNotNull(responseList);
        assertEquals(1, responseList.size());;
    }

    @Test
    public void testCreateProject() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setTitle("Test Project");
        projectRequest.setManagerId(1L);
        projectRequest.setEmployeeIds(Arrays.asList(2L, 3L, 4L));

        Employee manager = new Employee();
        manager.setId(1L);

        Employee employee1 = new Employee();
        employee1.setId(4L);

        Employee employee2 = new Employee();
        employee2.setId(5L);

        Employee employee3 = new Employee();
        employee3.setId(6L);

        when(employeeRepository.findById(eq(1L))).thenReturn(Optional.of(manager));
        when(employeeRepository.findAllById(any())).thenReturn(Arrays.asList(employee1, employee2, employee3));

        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setTitle("Test Project");
        savedProject.setManagerId(manager);
        savedProject.setEmployee(Arrays.asList(employee1, employee2, employee3));
        savedProject.setStatus(ProjectStatus.TODO);

        when(projectRepository.save(any())).thenReturn(savedProject);

        when(projectService.createProject(eq(projectRequest))).thenReturn(savedProject);

        ProjectEmployeeResponse managerResponse = new ProjectEmployeeResponse();
        managerResponse.setId(manager.getId());
        managerResponse.setFirstName(manager.getFirstName());
        managerResponse.setLastName(manager.getLastName());

        ProjectEmployeeResponse employeeResponse1 = new ProjectEmployeeResponse();
        employeeResponse1.setId(employee1.getId());

        ProjectEmployeeResponse employeeResponse2 = new ProjectEmployeeResponse();
        employeeResponse2.setId(employee2.getId());

        ProjectEmployeeResponse employeeResponse3 = new ProjectEmployeeResponse();
        employeeResponse3.setId(employee3.getId());

        ProjectResponse expectedProjectResponse = new ProjectResponse();
        expectedProjectResponse.setId(1L);
        expectedProjectResponse.setTitle("Test Project");
        expectedProjectResponse.setManagerId(managerResponse); // Set managerId as the manager's response
        expectedProjectResponse.setEmployees(Arrays.asList(employeeResponse1, employeeResponse2, employeeResponse3)); // Set employee responses
        expectedProjectResponse.setStatus(ProjectStatus.TODO);

        when(projectService.getProjectById(anyLong())).thenReturn(expectedProjectResponse);

        ResponseEntity<ProjectResponse> responseEntity = projectController.createProject(projectRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ProjectResponse projectResponse = responseEntity.getBody();
        assertNotNull(projectResponse);
        assertEquals(1L, projectResponse.getId());
        assertEquals("Test Project", projectResponse.getTitle());
        assertNotNull(projectResponse.getManagerId());
        assertEquals(3, projectResponse.getEmployees().size());
        assertEquals(ProjectStatus.TODO, projectResponse.getStatus());
    }



    @Test
    public void testUpdateProject() {
        Long projectId = 1L;
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setTitle("Updated Project");
        projectRequest.setManagerId(2L);
        projectRequest.setEmployeeIds(List.of(3L, 4L, 5L));
        projectRequest.setStatus(ProjectStatus.PROGRESS);

        Employee manager = new Employee();
        manager.setId(2L);

        Employee employee1 = new Employee();
        employee1.setId(3L);

        Employee employee2 = new Employee();
        employee2.setId(4L);

        Employee employee3 = new Employee();
        employee3.setId(5L);

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setTitle("Existing Project");
        existingProject.setManagerId(manager);
        existingProject.setEmployee(List.of(employee1, employee2));
        existingProject.setStatus(ProjectStatus.TODO);

        when(projectRepository.findById(eq(projectId))).thenReturn(Optional.of(existingProject));
        when(employeeRepository.findAllById(eq(projectRequest.getEmployeeIds())))
                .thenReturn(List.of(employee1, employee2, employee3));

        Project updatedProject = new Project();
        updatedProject.setId(projectId);
        updatedProject.setTitle("Updated Project");
        updatedProject.setManagerId(manager);
        updatedProject.setEmployee(List.of(employee1, employee2, employee3));
        updatedProject.setStatus(ProjectStatus.PROGRESS);

        when(projectService.updateProject(eq(projectRequest), any())).thenReturn(updatedProject);

        ResponseEntity<String> responseEntity = projectController.updateProject(projectId, projectRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Project with id " + projectId + " updated successfully", responseEntity.getBody());
    }

    @Test
    public void testUpdateProjectNotFound() {
        Long projectId = 1L;
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setTitle("Updated Project");
        projectRequest.setManagerId(2L);
        projectRequest.setEmployeeIds(List.of(3L, 4L, 5L));
        projectRequest.setStatus(ProjectStatus.PROGRESS);

        when(projectRepository.findById(eq(projectId))).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = projectController.updateProject(projectId, projectRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Project with ID " + projectId + " does not exist.", responseEntity.getBody());
    }

    @Test
    public void testDeleteProjectById() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);

        when(projectService.deleteProjectById(projectId)).thenReturn(ResponseEntity.ok("Project with ID 1 deleted"));

        ResponseEntity<String> response = projectController.deleteProject(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project with ID 1 deleted", response.getBody());

        verify(projectService, times(1)).deleteProjectById(projectId);
    }

    @Test
    public void testDeleteProjectByIdNotFound() {
        Long projectId = 2L;

        when(projectService.deleteProjectById(projectId))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Project with ID 2 doesn't exist"));

        ResponseEntity<String> response = projectController.deleteProject(projectId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Project with ID 2 doesn't exist", response.getBody());

        verify(projectService, times(1)).deleteProjectById(projectId);
    }


}
