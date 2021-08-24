package com.revature.persistence;

//import com.revature.util.ConnectionUtil;

import com.revature.util.ColumnField;
import com.revature.util.ConnectionUtil;

import java.lang.reflect.Field;
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

/**
 * DAO implementation
 */
public class SQLCommandDAOImpl implements DAO<Object> {
    /**
     * This is responsible for the "SELECT * FROM [table]" statement.
     * @param object from a class
     * @return Optional List of Objects
     */
    @Override
    public Optional<List<Object>> findAll(Object object) {
        List<Object> allEmployees = new ArrayList<>();
        List<String> listOfColumns = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            listOfColumns.add(field.getName());
            try{
                ColumnField columnField = new ColumnField(field);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try (Connection conn = ConnectionUtil.getConnection()) {

            String sql = "SELECT * FROM " + object.getClass().getSimpleName() + " ORDER BY " + listOfColumns.get(0) + ";";
            Statement s = conn.createStatement();
            s.executeQuery(sql);
            ResultSet rs = s.executeQuery(sql);
            Method methodCall1;

            while (rs.next()) {
                Object object2 = object.getClass().newInstance();
                for (int index = 0; index < listOfColumns.size(); index++) {
                    String setterMethodCreatorPart1 = object2.getClass().getDeclaredField(listOfColumns.get(index)).getName();
                    String setterMethodCreatorPart2 = "set" + setterMethodCreatorPart1.substring(0, 1).toUpperCase() + setterMethodCreatorPart1.substring(1);

                    if (index == 0) {
                        methodCall1 = object2.getClass().getDeclaredMethod(setterMethodCreatorPart2, int.class);
                        methodCall1.invoke(object2, rs.getObject(listOfColumns.get(index)));

                    } else {
                        methodCall1 = object2.getClass().getDeclaredMethod(setterMethodCreatorPart2, String.class);
                        methodCall1.invoke(object2, rs.getObject(listOfColumns.get(index)));
                    }
                    if(index == listOfColumns.size()-1){
                        allEmployees.add(object2);
                    }
                }

            }
            return Optional.of(allEmployees);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.of(allEmployees);
    }

    /**
     * This is responsible for the "SELECT * FROM [table] WHERE [ID] = [ID_Value]
     * @param object from a class
     * @param id number representing a primary key
     * @return An optional object with the row data.
     */
    @Override
    public Optional<Object> findById(Object object, int id) {
        List<String> listOfColumns = new ArrayList<>();
        List<String> classToAdd = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            listOfColumns.add(field.getName());
        }
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection conn = ConnectionUtil.getConnection()) {

            String sql = "SELECT * from " + object.getClass().getSimpleName() + " where " + listOfColumns.get(0) + " = " + id + ";";
            assert conn != null;
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
        return Optional.of(object);
    }

    /**
     * This is responsible for inserting a new row of data into the table. (INSERT INTO [table] VALUES[values])
     * @param object from a class. If a column isn't defined then it will appear as null in the table.
     * @return An integer representing the success of the Query.
     */
    @Override
    public int insert(Object object) {
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

        try (Connection conn = ConnectionUtil.getConnection()) {
            assert conn != null;
            String insertSQLCommand = "INSERT into " + object.getClass().getSimpleName() + " VALUES(" + valuesInsertData + ");";
            Statement s = conn.createStatement();
            return s.executeUpdate(insertSQLCommand);

        } catch (Exception e) {
            //System.out.println("\nPrimary key already exists.\nClass object from class [" + object.getClass().getSimpleName() + "] was not inserted into the table.");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * This is responsible for updating an existing entry in the table. From postman, all columns must be provided or they will be null.
     * @param object from a class.
     * @param id representing a primary key, as well as a class object.
     * @return boolean representing success of the update.
     */
    @Override
    public boolean update(Object object, int id) {
        int success = 0;
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
                    fieldCount++;
                } else {
                    valuesInsertData.append(field.getName()).append(" = '").append(getterMethodCall.invoke(object)).append("', ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        valuesInsertData = new StringBuilder(valuesInsertData.substring(0, valuesInsertData.length() - 2));

        try (Connection conn = ConnectionUtil.getConnection()) {
            assert conn != null;
            String insertSQLCommand = "UPDATE " + object.getClass().getSimpleName() + " SET " + valuesInsertData + " WHERE " + listOfColumns.get(0) + " = " + id + ";";
            Statement s = conn.createStatement();
            success = s.executeUpdate(insertSQLCommand);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return success == 1;
    }

    /**
     * This is responsible for deleting an existing user from the table. (DELETE FROM [table] WHERE [ID] = [ID_value]
     * @param object from a class and
     * @param id integer representing a primary key
     * @return boolean representing success of the delete.
     */
    @Override
    public boolean delete(Object object, int id) {
        int success = 0;
        List<String> listOfColumns = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            listOfColumns.add(field.getName());
        }
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection conn = ConnectionUtil.getConnection()) {
            assert conn != null;
            String insertSQLCommand = "DELETE FROM " + object.getClass().getSimpleName() + " WHERE " + listOfColumns.get(0) + " = " + id + ";";
            Statement s = conn.createStatement();
            success = s.executeUpdate(insertSQLCommand);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success == 1;
    }
}