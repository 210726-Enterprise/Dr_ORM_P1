package com.revature;

import com.revature.models.employees;
import com.revature.persistence.SQLCommandDAOImpl;

public class Main {
    public static void main(String[] args) {

        employees employee = new employees();
        SQLCommandDAOImpl SQLCommandDAOImpl = new SQLCommandDAOImpl();



        //String clazz = employee.getClass().getSimpleName();


//        SQLCommandDAOImpl.findAll(employee);
//        System.out.println("");
//        SQLCommandDAOImpl.findById(employee, 2);

//        employees employee1 = new employees();
//        employee1.setEmployee_id(1);
//        employee1.setFirst_name("Dhawal");
//        employee1.setLast_name("Sandesara");
//        //employee1.setEmail("dhawal@gmail.com");
//        SQLCommandDAOImpl.insert(employee1);
//        System.out.println();

//        employees employee2 = new employees();
//        employee2.setFirst_name("DHAWAL");
//        SQLCommandDAOImpl.update(employee, 1);
        employees allEmployees = new employees();
        SQLCommandDAOImpl.findAll(allEmployees);

        employees dhawal = new employees();
//        SQLCommandDAOImpl.findById(dhawal, 1);
//        dhawal.setEmail("dhawal96@gmail.com");
//        SQLCommandDAOImpl.update(dhawal, 1);

        SQLCommandDAOImpl.delete(dhawal, 1);

    }
}
