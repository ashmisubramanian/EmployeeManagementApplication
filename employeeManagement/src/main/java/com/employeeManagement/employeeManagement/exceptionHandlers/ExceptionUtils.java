package com.employeeManagement.employeeManagement.exceptionHandlers;

public class ExceptionUtils {
    public static class EmployeeIsAssignedException extends RuntimeException {

        public EmployeeIsAssignedException(String message) {
            super(message);
        }
    }
}
