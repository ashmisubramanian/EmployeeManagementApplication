package com.employeeManagement.employeeManagement.DTO;

import com.employeeManagement.employeeManagement.enumModel.Role;

import java.util.List;

public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    private List<String> projectTitles;


    public EmployeeResponse(long id, String firstName, String lastName, String email, Role role) {
        this.id=id;
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.role=role;
    }
    public EmployeeResponse(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<String> getProjectTitles() {
        return projectTitles;
    }

    public void setProjectTitles(List<String> projectTitles) {
        this.projectTitles = projectTitles;
    }
}
