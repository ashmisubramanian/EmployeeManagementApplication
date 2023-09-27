package com.employeeManagement.employeeManagement.DTO;

import com.employeeManagement.employeeManagement.enumModel.ProjectStatus;
import com.employeeManagement.employeeManagement.model.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ProjectRequest {
    @NotBlank(message = "Title should not be null")
    private String title;
    @NotNull(message = "Manager ID should not be null")
    private Long managerId;

    private List<Long> employeeIds;
    private ProjectStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public ProjectRequest(){}
}

