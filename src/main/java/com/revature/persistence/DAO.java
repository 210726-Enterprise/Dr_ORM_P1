package com.revature.persistence;

import java.util.List;
import java.util.Optional;

/**
 * DAO for SQL Command Implementation
 * @param <T>
 */
public interface DAO<T> {
      Optional<List<Object>> findAll(Object object);
      Optional<Object> findById(Object object, int t);
      int insert (Object object);
      boolean update(Object object, int t);
      boolean delete(Object object, int t);
}