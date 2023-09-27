package com.employeeManagement.employeeManagement.controller;

import com.employeeManagement.employeeManagement.DTO.Login;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.security.AuthenticationResponse;
import com.employeeManagement.employeeManagement.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SecurityController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody Login login){
        return ResponseEntity.ok(authenticationService.login(login));
    }
}
