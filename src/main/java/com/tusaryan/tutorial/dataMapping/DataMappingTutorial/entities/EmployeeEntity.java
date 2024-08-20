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
//Builder will allow to create object of employee entity with the help of builder pattern. used to optimise two api calls in department service
@Builder
@Table(name = "employees")
public class EmployeeEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    //now this is bidirectional mapping. we are mapping it one-to-one with the manager mapping defined inside the department entity. Now this will utilize the prev mapping.
    //due to this department entity will not create another column of manager inside employee entity
    //it is the job of department table to store all the information needed about that department.
    @OneToOne(mappedBy = "manager")
    /* what we have done is Each department has an employee and each employee has a department, that's why we are getting the recursive call to the same serialization and deserializing.
    * to Fix this recursive call use this inside EmployeeEntity mapping : @JsonIgnore -> now Jackson will ignore this field and will not serialize or deserializing this field.
    * */
    @JsonIgnore
    //if we look at sql database then employee table itself does not have any managed_department/department id
    //its just that department entity/table has a column called department manager.
    private DepartmentEntity managedDepartment;

    //unidirectional many to one mapping if not used mappedBy in Department Entity.
//    many worker can work at a single department. but one department can have many employees
    //So from department to employee One to Many.
    //from Employee to Department -> Many to One
    // referencedColumn store the ref of column in department table. can skip it as by default it map to "id". It is better to use if e have different column name of id in DB and different Java object/variable name.
    //cascade = CascadeType.ALL -> To propagate the operation from Parent(Owner of relationship mapping/foreign key-holder) to Child. whenever are doing any save operation on Employee Entity, I want that effect to propagate to Department Entity as well.
    //eg: if we try to remove an employee from department/employee Entity than that effect should also take place inside Department Entity as well.
    //Since this is a Many-to-One mapping. So, any changes in Employee Entity that change will automatically reflect inside Department Entity. Because our Department Entity is actually not aware of workerDepartment id.
    //CascadeType.PERSIST -> will take effect when we try to save something. CascadeType.REFRESH -> when we PUT/update something.
    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "worker_department_id", referencedColumnName = "id")
//    @JoinColumn(name = "worker_department_id")

    // if we don't want to store information about the department inside our employee table in that case we use @JoinTable.
    //creating new table for this mapping/to store the data, now employee repo will no longer have the above-mentioned "worker_department_id" column. Can also define joinColumns and inverseJoinColumns inside it.
    @JoinTable(name = "worker_department_mapping")
    //Either of the bidirectional mapping has to have a @JsonIgnore to fix recursive calls. Also, we will not find that field (here "workerDepartment") in that entity DB where we define @JsonIgnore.
    @JsonIgnore
    private DepartmentEntity workerDepartment;
    /**Problem
     * Our database is properly connected. Can fetch department using get Employee by id. But our bidirectional mapping is not working i.e. unable to fetch Employee by department id.
     * Problem : Problem is not related to DB but related with Lombok
     * Reason: when we don't define our hashcode and equal method properly inside Lombok. for now @Data from lombok is creating the hashcode and equal method in our behalf.
     * Recommendation : We should avoid using @Data when working with bidirectional mapping in JPA. use your own hashcode and equal mapping. @Data will suffice only in unidirectional mapping.
     * */


    //in many-to-many mapping any of the entities to be the owner, it does not matter which. We'll create another mapping / new sql table which will hold the relationship/ownership of mapping.
    //freelancer -> each department has multiple freelancers and each freelancer can work in multiple departments.
    @ManyToMany
    //this will create a new table with two column -> employee_id, department_id
    @JoinTable(name = "freelancer_department_mapping",
            //joinColumns -> by default created by id/ can change name of join column.
            joinColumns = @JoinColumn(name = "employee_id"),
            //the other one/ other entity
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    //to avoid recursion mapping
    @JsonIgnore
    private Set<DepartmentEntity> freelanceDepartments;

    //cmd+N -> equals() and hashcode() -> select "accept subclasses/class type comparison instanceOf" and "use getter", next -> un-tick/exclude all the entities
    //Hashcode and equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeEntity that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
