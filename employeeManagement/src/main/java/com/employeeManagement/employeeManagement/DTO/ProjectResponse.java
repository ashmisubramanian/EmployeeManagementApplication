package com.employeeManagement.employeeManagement.DTO;

import com.employeeManagement.employeeManagement.enumModel.ProjectStatus;

import java.util.List;

public class ProjectResponse {

    private Long id;
    private String title;
    private ProjectEmployeeResponse managerId;
    private List<ProjectEmployeeResponse> employees;
    private ProjectStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProjectEmployeeResponse getManagerId() {
        return managerId;
    }

    public void setManagerId(ProjectEmployeeResponse managerId) {
        this.managerId = managerId;
    }

    public List<ProjectEmployeeResponse> getEmployees() {
        return employees;
    }

    public void setEmployees(List<ProjectEmployeeResponse> employees) {
        this.employees = employees;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
