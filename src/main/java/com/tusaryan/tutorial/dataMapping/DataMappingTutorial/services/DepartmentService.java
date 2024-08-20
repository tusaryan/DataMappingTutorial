package com.tusaryan.tutorial.dataMapping.DataMappingTutorial.services;

import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.DepartmentEntity;
import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities.EmployeeEntity;
import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories.DepartmentRepository;
import com.tusaryan.tutorial.dataMapping.DataMappingTutorial.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    //one of the benefit of MVC architecture that now we can use any repository inside our Service
    //to access Employee Repo inside our department.
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public DepartmentEntity createNewDepartment(DepartmentEntity departmentEntity) {
        return departmentRepository.save(departmentEntity);
    }

    public DepartmentEntity getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public DepartmentEntity assignManagerToDepartment(Long departmentId, Long employeeId) {
        //to fetch the department to assign its manager, now we're assuming department with this id is present. Else we will throw exception using ".orElseThrow()"
        //DepartmentEntity departmentEntity = departmentRepository.findById(departmentId).orElse(null);
        //alternate method
        Optional<DepartmentEntity> departmentEntity = departmentRepository.findById(departmentId);

        //get that Employee
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(employeeId);
        //EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).orElse(null);

        //mapping the manager i.e. employee with the department
        //to get the department. "department" used just below in .map is just a variable name to take that particular department and map it with the manager
        //always use flapMap when dealing with nested map
        return departmentEntity.flatMap(department ->
                //to get the employee
                employeeEntity.map(employee -> {
                    //add/set manager i.e. employee to the department. this is currently inside our java object
                    department.setManager(employee);

                    //now saving it to DB and to perform that operation, that method is present inside our Department Repo
                    //save this new department created inside repo
                    return departmentRepository.save(department);
                })).orElse(null);
    }

    public DepartmentEntity assignedDepartmentOfManager(Long employeeId) {
        /*Now we have two ways of doing this
        * 1st -> from department we can get the manager which has this employeeId
        * 2nd -> we can directly find the employee from employeeId and get its managed_department field.
        *  */


        //get employee by ID
        //Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(employeeId);

        //optimised to skip two api calls-> using builder pattern defined inside lombok, to create EmployeeEntity object
        EmployeeEntity employeeEntity = EmployeeEntity.builder().id(employeeId).build();

        /*
        //2nd way
        //using Lambda
        return employeeEntity.map(employee -> employee.getManagedDepartment()).orElse(null);

        //using method reference
        // we are mapping to employeeEntity and then whatever value we are getting, we are calling the getManagedDepartment function on this.
        //return employeeEntity.map(EmployeeEntity::getManagedDepartment).orElse(null);
         */

        //1st way
        //click on findByManager and then click option+return to create method inside dept repo
        //using that employee (employeeEntity) we are calling the department and finding manager. so, here we are calling two APIs/two DB calls departmentRepository, employeeEntity which calls employeeRepo;
        //return departmentRepository.findByManager(employeeEntity.get());

        //optimised two api calls. now we are doing one api call of department repo.
        //this findByManager only need employeeId to find the department. earlier we are passing the title/other fields too now rest of the fields inside employeeId will be empty and only id will be passed
        return departmentRepository.findByManager(employeeEntity);
    }

    public DepartmentEntity assignWorkerToDepartment(Long departmentId, Long employeeId) {
        Optional<DepartmentEntity> departmentEntity = departmentRepository.findById(departmentId);

        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(employeeId);

        return departmentEntity.flatMap(department ->
                employeeEntity.map(employee -> {
                    //set the department of an employee
                    employee.setWorkerDepartment(department);
                    //save the new data to employee repo
                    employeeRepository.save(employee);

                    //to fetch the data back. remember we are not saving it again in DB
                    department.getWorkers().add(employee);
                    return department;
                })).orElse(null);
    }

    public DepartmentEntity assignFreelancerToDepartment(Long departmentId, Long employeeId) {
        Optional<DepartmentEntity> departmentEntity = departmentRepository.findById(departmentId);

        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(employeeId);

        return departmentEntity.flatMap(department ->
                employeeEntity.map(employee -> {
                    //can go to either of the entities and save employee there, and it's all done. Since we have set cascade to ALL, so it will automatically update the other entity for us. getting some issues here using department to save will resolve later.
                    //calling save method now in employee entity as it has the @JoinTable and freelanceDepartment. So, it is making more sense to call save method on employee entity and not department entity.
                    //to assign/add department to employee/freelancer
                    employee.getFreelanceDepartments().add(department);

                    //calling the save method on employee to save that same employee
                    employeeRepository.save(employee);

                    //adding this employee to the department so that we can return the fresh department.
                    department.getFreelancers().add(employee);
                    //Return this newly created department
                    return department;
                })).orElse(null);
    }
}