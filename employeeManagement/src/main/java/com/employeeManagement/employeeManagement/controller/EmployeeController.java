package com.employeeManagement.employeeManagement.controller;

import com.employeeManagement.employeeManagement.DTO.EmployeeResponse;
import com.employeeManagement.employeeManagement.enumModel.Role;
import com.employeeManagement.employeeManagement.exceptionHandlers.ExceptionUtils;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import com.employeeManagement.employeeManagement.security.AuthenticationService;
import com.employeeManagement.employeeManagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;


    @GetMapping
    public @ResponseBody List<EmployeeResponse> getEmployees(){
        return employeeService.getEmployees();
    }

    @GetMapping(params = "id")
    public @ResponseBody ResponseEntity<EmployeeResponse> findEmployeeById(@RequestParam(name = "id") Long id){
        EmployeeResponse employeeResponse=employeeService.getEmployeeById(id);
        if (employeeResponse!=null){
            return ResponseEntity.ok(employeeResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> saveEmployee(@Valid @RequestBody Employee saveEmployee){
        saveEmployee.setPassword(passwordEncoder.encode(saveEmployee.getPassword()));
        saveEmployee.setRole(Role.USER);
        Employee employee= employeeRepository.save(saveEmployee);
        return ResponseEntity.ok(employeeService.getEmployeeById(employee.getId()));
    }

    @PreAuthorize("(hasAuthority('ADMIN') or (hasAuthority('USER') and principal.username==@authenticationService.userNameForId(#id)))")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id,@Valid @RequestBody Employee updateEmployee) {
        String role="USER";
        Optional<Employee> employeePresent = employeeRepository.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(employeePresent.isPresent()){
            Employee employee=employeePresent.get();
            if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
               role="ADMIN";
            }
            Employee employee1=employeeService.updateEmployee(updateEmployee,employee,role);
            if(employee1!=null){
                employeeRepository.saveAndFlush(employee1);
                return ResponseEntity.ok("Employee with id "+id+" updated successfully");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't have permission to update your role.");
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee with ID " + id + " does not exist.");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id){
        Optional<Employee> employee= employeeRepository.findById(id);
        if(employee.isPresent()){
            List<Long> projectIds=employeeService.deleteEmployee(id);
            if(projectIds.isEmpty()){
                employeeRepository.deleteById(id);
                return ResponseEntity.ok("Employee with Id {"+id+"} deleted");
            }
            else {
                throw new ExceptionUtils.EmployeeIsAssignedException("The employee is assigned to projects"+projectIds+".So remove employee with id{"+id+"} from projects"+projectIds+".");
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Employee with Id {"+id+"} doesn't exist");
        }
    }
}
