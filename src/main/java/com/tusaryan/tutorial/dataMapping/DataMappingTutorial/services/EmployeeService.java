package com.tusaryan.tutorial.dataMapping.DataMappingTutorial.services;

import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.EmployeeEntity;
import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories.DepartmentRepository;
import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeEntity createNewEmployee(EmployeeEntity employeeEntity) {
        return employeeRepository.save(employeeEntity);
    }

    public EmployeeEntity getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
}
