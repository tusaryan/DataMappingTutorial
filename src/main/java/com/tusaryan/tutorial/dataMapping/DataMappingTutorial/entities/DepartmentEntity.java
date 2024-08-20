package com.tusaryan.tutorial.dataMapping.DataMappingTutorial.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departments")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    //FetchType.EAGER -> means it will get data of manager as soon as it is getting the data of department.
    //this department has OneToOne mapping with this manager.
    //this is a foreign key (i.e. manager id) inside department entity
    //this is now unidirectional mapping -> i.e. department knows which employee is manager but that employee does not know whether he is manager or not?
    //so to achieve bidirectional mapping use this one-to-one mapping in EmployeeEntity
    /**
     * currently the fetch property defined inside one-to-one is set to "EAGER"
     *so it is directly calling the data to get the manager as well and that has a drawback.
     * now after assigning the manager if we get same department by ID
     * it run into a recursive loop of calling itself i.e. the data we will get will have manager too
     * And inside we have nested managedDepartment field which is same department i.e. "HR" (name used during testing)
     * and now due to repeated recursive call we have continuous nested fields of manager and managedDepartment.
     * this is due to the recursive call of serialization that is defined by Jackson (Jackson takes care of serializing and deserializing the Json data to the Java Objects)
     * and what we have done is Each department has an employee and each employee has a department, that's why we are getting the recursive call to the same serialization and deserializing.
     *
     * to Fix it use this inside EmployeeEntity mapping : @JsonIgnore -> now Jackson will ignore this and will not serialize or deserializing this field.
     * */
    @OneToOne
    @JoinColumn(name = "department_manager") //to change(default : manager_id)/create a column in DB named "department_manager"
    //its just that department entity/table has a column called department manager.
    private EmployeeEntity manager;

    /** General Thumb Rule:
     * we will define @JoinColumn/@JoinTable in one Entity
     * and in other side of Entity we'll only define "mappedBy" field i.e "@OneToOne(mappedBy = "manager")"
     */

    /** Examples of various mappings:
     * OneToOne -> One Department can have One Manager(i.e. mapped with Employee) (having a manager field in department Repo)/ One Employee can be a worker in One Department (having a department field in employee Repo)
     * OneToMany -> One Department can have many workers (all these workers are employee) (worker field in employee Repo, having List of Employee)
     * for the above mapping who will be having the ownership or the foreign key?
     * one department can have many employees. And inside our tables, one department cannot store a list of employees
     * But each employee have one department. So, inside our employee we'll store the foreign key i.e. department id.
     * so in our sql DB there will be a column of department, even department does not know who all are the workers in their department.
     * but we can access all of them using mvc architecture and Hibernate methods.
     * */

    //this will not create a new column unless we add @JoinColumn()
    //or if we use mappedBy it will know that it is not a new field. so, no new column will be created. if mapped by is not mentioned then it can create a new column/table
    //FetchType.EAGER -> default, to fetch the related mapped entity as soon as we request/get the department.
    //FetchType.LAZY -> (good practice to use it) only fetch/get the related entity when a method is called like getWorkers().
    //using lazy we are getting the workers in getDepartment by id api request. this is due to Jackson, it is calling getWorkers. But if we don't want that "worker" field when calling department by id when can also do that so in FetchType.LAZY behaviour.

    @OneToMany(mappedBy = "workerDepartment", fetch = FetchType.LAZY)
    //defining set not list. Reason: employees/workers are unique in a department
    private Set<EmployeeEntity> workers;

    @ManyToMany(mappedBy = "freelanceDepartments")
    private Set<EmployeeEntity> freelancers;

    //own implementation of Hashcode and equals -> remove @Data -> add @Getter & @Setter -> auto generate using, cmd+N -> equals and hashcode -> similar steps as mentioned in Employee Entity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentEntity that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle());
    }
}
