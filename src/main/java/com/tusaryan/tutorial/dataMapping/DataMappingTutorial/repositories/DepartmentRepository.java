package com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories;

import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.DepartmentEntity;
import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

    //auto generate method using hibernate
    //this is a valid query method because we have manager field inside our department Entity
    DepartmentEntity findByManager(EmployeeEntity employeeEntity);
}
