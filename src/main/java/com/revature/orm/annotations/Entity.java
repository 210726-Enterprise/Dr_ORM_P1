package com.revature.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

// Entities in JPA are nothing but POJOs representing data that can be persisted to the database.
// An entity represents a table stored in a database.
// Every instance of an entity represents a row in the table.
@Target(ElementType.TYPE)
@Retention (RetentionPolicy.RUNTIME)
public @interface Entity {
    String entityName();
}