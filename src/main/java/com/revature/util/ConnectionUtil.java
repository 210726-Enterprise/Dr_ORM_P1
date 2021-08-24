package com.revature.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
 * Singleton Design Pattern: Design patterns are commonly used ways of instantiating an object
 * and defining particular members or fields of that class.
 *
 * A singleton design pattern is a Design Pattern for classes when they are restricted to
 * only ever being instantiated ONCE.
 *
 * Characteristics of a Singleton Design Pattern
 *
 * - private constructors
 * - Static field of an instance of this class
 * - Leverage a public static getInstance() method
 */
public class ConnectionUtil {
    private static Connection conn = null;

    private static final Logger log = LoggerFactory.getLogger(ConnectionUtil.class);

    // Notice how weird this is...Only for singleton (for now...)
    private ConnectionUtil(){
        super();
    }

    // this is our getInstance() method
    public static synchronized Connection getConnection() {

        try {
            if (conn  != null && !conn.isClosed()) {
                return conn;
            }
        } catch (SQLException e) {
            log.error("We failed to reuse a Connection", e);
            return null;
        }

        Properties prop = new Properties();

        try {

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(System.getenv("db_url"),
                    System.getenv("db_username"),
                    System.getenv("db_password"));
            log.info("Database connection established!");
        } catch (SQLException e) {
            log.error("We failed to establish a Connection");
            return null;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }

        return conn;

    }
}
