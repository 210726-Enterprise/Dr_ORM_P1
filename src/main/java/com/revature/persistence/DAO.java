package com.revature.persistence;

public interface DAO<T> {
      void findAll(Object object);
      void findById(Object object, int t);
      boolean insert (Object object);
      boolean update(Object object, int t);
      boolean delete(Object object, int t);
}