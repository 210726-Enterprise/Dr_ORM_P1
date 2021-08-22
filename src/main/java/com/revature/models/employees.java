package com.revature.models;

import com.revature.orm.annotations.Column;
import com.revature.orm.annotations.Entity;
import com.revature.orm.annotations.Id;
import com.revature.orm.annotations.Table;

import java.lang.annotation.Target;

@Entity(entityName = "employees")
@Table(tableName = "employees")
public class employees {

    @Id(columnName = "employee_id")
    int employee_id;

    @Column(columnName = "first_name")
    String first_name;

    @Column(columnName = "last_name")
    String last_name;

    @Column(columnName = "email")
    String email;

    public employees(){ }

    public employees(String s){}

    public employees(int employee_id, String first_name, String last_name, String email) {
        this.employee_id = employee_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employee_id='" + employee_id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
