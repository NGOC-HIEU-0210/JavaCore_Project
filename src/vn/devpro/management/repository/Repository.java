package vn.devpro.management.repository;

import java.util.List;
import java.util.Optional;

import vn.devpro.management.model.BaseEntity;


public interface Repository<T extends BaseEntity> {

    T save(T entity);

    Optional<T> findById(int id);

    Optional<T> findByCode(String code);

    List<T> findAll();

    boolean delete(T entity);
}
