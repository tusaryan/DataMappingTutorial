package com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories;

import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

}
