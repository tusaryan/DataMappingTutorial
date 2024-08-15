package com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories;

import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

}
