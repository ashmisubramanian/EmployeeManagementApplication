package com.employeeManagement.employeeManagement.model;

import com.employeeManagement.employeeManagement.enumModel.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name",nullable = false)
    @NotBlank(message = "First name should not be null")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email",nullable = false)
    @NotBlank(message = "Email should not be null")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password",nullable = false)
    @NotBlank(message = "Password should not be null")
    @Pattern(regexp ="^(?=.*[0-9])(?=.*[A-Z]).{8,}$", message = "Password must follow the pattern")
    private String password;


    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    //@JsonManagedReference
    @JsonBackReference
    private List<Project> project;

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


    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
    }

    public Employee(){}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }



    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
