package com.revature.persistence;

import com.revature.models.employees;
import com.sun.deploy.util.StringUtils;
import org.postgresql.util.PSQLException;
//import com.revature.util.ConnectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * JDBC API:
 *
 * DriverManager class
 * 	- Some static methods, such as getConnection that we use to connect to a DB
 * 	- Used to obtain a Connection
 *
 * Connection Interface
 * 	- Represents a Connection to our DB
 * 	- Has methods to obtain Statements
 *
 * Statement Interface
 * 	- Represents a SQL statement that will be performed against the DB
 * 	- There are sub-interfaces for specific use-cases
 * 	- PreparedStatement Interface
 * 		- CallableStatement Interface
 * 	- Have methods to obtain ResultSets
 *
 * ResultSet Interface
 * 	- Represents data obtained from the DB
 * 	- Follows an "Iterator" structure
 * 		- Is pointing to individual rows
 * 		- Invoke the .next() method to step forward
 * 		- Starts at the position BEFORE the first row
 * 	- Has methods to obtain data from individual columns for that row
 * 		- getInt
 * 		- getString
 * 		- etc
 */
public class SQLCommandDAOImpl implements DAO<Object> {

    @Override
    public void findAll(Object object) {
        List<String> listOfColumns = new ArrayList<>();
        List<String> classToAdd = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            listOfColumns.add(field.getName());
        }
        try (Connection conn = DriverManager.getConnection(System.getenv("db_url"),
                System.getenv("db_username"),
                System.getenv("db_password"))) {

            String sql = "SELECT * FROM " + object.getClass().getSimpleName() + ";";
            Statement s = conn.createStatement();
            s.executeQuery(sql);
            ResultSet rs = s.executeQuery(sql);
            Method methodCall1;

            while (rs.next()) {
                for (int index = 0; index < listOfColumns.size(); index++) {
                    String setterMethodCreatorPart1 = object.getClass().getDeclaredField(listOfColumns.get(index)).getName();
                    String setterMethodCreatorPart2 = "set" + setterMethodCreatorPart1.substring(0, 1).toUpperCase() + setterMethodCreatorPart1.substring(1);

                    if (index == 0) {
                        methodCall1 = object.getClass().getDeclaredMethod(setterMethodCreatorPart2, int.class);
                        methodCall1.invoke(object, rs.getObject(listOfColumns.get(index)));
                    } else {
                        methodCall1 = object.getClass().getDeclaredMethod(setterMethodCreatorPart2, String.class);
                        methodCall1.invoke(object, rs.getObject(listOfColumns.get(index)));
                    }
                }

                classToAdd.add(object.toString());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String element : classToAdd) {
            System.out.println(element);
        }
    }

    @Override
    public void findById(Object object, int id) {
        List<employees> allEmployees = new ArrayList<>();
        List<String> listOfColumns = new ArrayList<>();
        List<String> classToAdd = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            listOfColumns.add(field.getName());
        }

        try (Connection conn = DriverManager.getConnection(System.getenv("db_url"), System.getenv("db_username"), System.getenv("db_password"))) {
            String sql = "SELECT * from " + object.getClass().getSimpleName() + " where employee_id = " + id + ";";
            assert conn != null;
            Statement s = conn.createStatement();
            s.executeQuery(sql);
            ResultSet rs = s.executeQuery(sql);
            Method methodCall1, methodCall2;

            while (rs.next()) {
                for (int index = 0; index < listOfColumns.size(); index++) {
                    String setterMethodCreatorPart1 = object.getClass().getDeclaredField(listOfColumns.get(index)).getName();
                    String setterMethodCreatorPart2 = "set" + setterMethodCreatorPart1.substring(0, 1).toUpperCase() + setterMethodCreatorPart1.substring(1);

                    if (index == 0) {
                        methodCall1 = object.getClass().getDeclaredMethod(setterMethodCreatorPart2, int.class);
                        methodCall1.invoke(object, rs.getObject(listOfColumns.get(index)));
                    } else {
                        methodCall1 = object.getClass().getDeclaredMethod(setterMethodCreatorPart2, String.class);
                        methodCall1.invoke(object, rs.getObject(listOfColumns.get(index)));
                    }
                }
                classToAdd.add(object.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String element : classToAdd) {
            System.out.println(element);
        }
    }

    @Override
    public boolean insert(Object object) {
        List<String> listOfColumns = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        String getterMethod = null;
        Method getterMethodCall;
        StringBuilder valuesInsertData = new StringBuilder();
        int fieldCount = 0;
        for (Field field : fields) {
            listOfColumns.add(field.getName());
            getterMethod = field.getName();
            try {
                getterMethod = "get" + getterMethod.substring(0, 1).toUpperCase() + getterMethod.substring(1);
                getterMethodCall = object.getClass().getDeclaredMethod(getterMethod);
                if (fieldCount == 0) {
                    valuesInsertData.append(getterMethodCall.invoke(object)).append(", '");
                    fieldCount++;
                } else {
                    valuesInsertData.append(getterMethodCall.invoke(object)).append("', '");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        valuesInsertData = new StringBuilder(valuesInsertData.substring(0, valuesInsertData.length() - 3));

        try (Connection conn = DriverManager.getConnection(System.getenv("db_url"), System.getenv("db_username"), System.getenv("db_password"))) {
            assert conn != null;
            String insertSQLCommand = "INSERT into " + object.getClass().getSimpleName() + " VALUES(" + valuesInsertData + ");";
            Statement s = conn.createStatement();
            s.executeUpdate(insertSQLCommand);

        } catch (Exception e) {
            System.out.println("\nPrimary key already exists.\nClass object from class [" + object.getClass().getSimpleName() + "] was not inserted into the table.");
            ;
        }
        return true;
    }

    @Override
    public boolean update(Object object, int id) {
        List<String> listOfColumns = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        String getterMethod = null;
        Method getterMethodCall;
        StringBuilder valuesInsertData = new StringBuilder();
        StringBuilder updatedValues = new StringBuilder();
        int fieldCount = 0;
        for (Field field : fields) {
            listOfColumns.add(field.getName());
            getterMethod = field.getName();
            try {
                getterMethod = "get" + getterMethod.substring(0, 1).toUpperCase() + getterMethod.substring(1);
                getterMethodCall = object.getClass().getDeclaredMethod(getterMethod);
                if (fieldCount == 0) {
                    fieldCount++;
                } else {
                    valuesInsertData.append(field.getName()).append(" = '").append(getterMethodCall.invoke(object)).append("', ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        valuesInsertData = new StringBuilder(valuesInsertData.substring(0, valuesInsertData.length() - 2));

        try (Connection conn = DriverManager.getConnection(System.getenv("db_url"), System.getenv("db_username"), System.getenv("db_password"))) {
            assert conn != null;
            String insertSQLCommand = "UPDATE " + object.getClass().getSimpleName() + " SET " + valuesInsertData + " WHERE " + listOfColumns.get(0) + " = " + id + ";";
            Statement s = conn.createStatement();
            s.executeUpdate(insertSQLCommand);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean delete(Object object, int id) {
        List<String> listOfColumns = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            listOfColumns.add(field.getName());
        }

        try (Connection conn = DriverManager.getConnection(System.getenv("db_url"), System.getenv("db_username"), System.getenv("db_password"))) {
            assert conn != null;
            String insertSQLCommand = "DELETE FROM " + object.getClass().getSimpleName() + " WHERE " + listOfColumns.get(0) + " = " + id + ";";
            Statement s = conn.createStatement();
            s.executeUpdate(insertSQLCommand);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}