package com.employeeManagement.employeeManagement.controller;

import com.employeeManagement.employeeManagement.DTO.EmployeeResponse;
import com.employeeManagement.employeeManagement.enumModel.Role;
import com.employeeManagement.employeeManagement.exceptionHandlers.ExceptionUtils;
import com.employeeManagement.employeeManagement.model.Employee;
import com.employeeManagement.employeeManagement.repository.EmployeeRepository;
import com.employeeManagement.employeeManagement.repository.ProjectRepository;
import com.employeeManagement.employeeManagement.security.AuthenticationService;
import com.employeeManagement.employeeManagement.service.EmployeeService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employee Management")
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

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @Operation(
            description = "GET endpoint for Employee",
            summary = "GET endpoint to retrieve all employees information",
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
    public @ResponseBody List<EmployeeResponse> getEmployees(){
        return employeeService.getEmployees();
    }


    @Operation(
            description = "GET endpoint for Employee",
            summary = "GET endpoint to retrieve an employee by id",
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
    public @ResponseBody ResponseEntity<EmployeeResponse> findEmployeeById(@RequestParam(name = "id") Long id){
        EmployeeResponse employeeResponse=employeeService.getEmployeeById(id);
        if (employeeResponse!=null){
            return ResponseEntity.ok(employeeResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponse);
    }


    @Operation(
            description = "POST endpoint for Employee",
            summary = "POST endpoint to add an employee",
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
    public ResponseEntity<EmployeeResponse> saveEmployee(@Valid @RequestBody Employee saveEmployee){
        return employeeService.saveEmployee(saveEmployee);
    }


    @Operation(
            description = "PUT endpoint for Employee",
            summary = "PUT endpoint to update an employee",
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
    @PreAuthorize("(hasAuthority('ADMIN') or (hasAuthority('USER') and principal.username==@authenticationService.userNameForId(#id)))")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Long id,@Valid @RequestBody Employee updateEmployee) {
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


    @Operation(
            description = "DELETE endpoint for Employee",
            summary = "DELETE endpoint to delete an employee",
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
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id){
        Optional<Employee> employee= employeeRepository.findById(id);
        if(employee.isPresent()){
            List<Long> projectIds=employeeService.deleteEmployee(id);
            if(projectIds.isEmpty()){
                employeeRepository.deleteById(id);
                return ResponseEntity.ok("Employee with Id {"+id+"} deleted");
            }
            else {
                throw new ExceptionUtils.EmployeeIsAssignedException("The employee part of" +
                        " projects"+projectIds+".So remove employee with id{"+id+"} from projects"+projectIds+".");
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Employee with Id {"+id+"} doesn't exist");
        }
    }
}
