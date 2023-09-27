package com.employeeManagement.employeeManagement.controller;

import com.employeeManagement.employeeManagement.DTO.Login;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.security.AuthenticationResponse;
import com.employeeManagement.employeeManagement.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    /*@PostMapping("/login")
    public ResponseEntity<String> loginValidation(@RequestBody Login login){
        Employee user=employeeRepository.findByEmail(login.getEmail());
        if(user!=null&&passwordEncoder.matches(login.getPassword(),user.getPassword())){
            return ResponseEntity.ok("Login Successful.");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login Unsuccessful.");
        }
    }*/
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody Login login){
        return ResponseEntity.ok(authenticationService.login(login));
    }
}
