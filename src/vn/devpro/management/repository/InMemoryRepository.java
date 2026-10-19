package vn.devpro.management.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import vn.devpro.management.model.BaseEntity;


public class InMemoryRepository<T extends BaseEntity> implements Repository<T> {
    private final String codePrefix;
    private final List<T> items = new ArrayList<>();
    private int nextId = 1;

    public InMemoryRepository(String codePrefix) {
        this.codePrefix = codePrefix;
    }

    @Override
    public T save(T entity) {
        if (entity.isNew()) {
            entity.setId(nextId++);
            entity.setCode(codePrefix + (100 + entity.getId()));
            items.add(entity);
            return entity;
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == entity.getId()) {
                items.set(i, entity);
                return entity;
            }
        }
        throw new IllegalArgumentException("Entity khong ton tai trong repository: id=" + entity.getId());
    }

    @Override
    public Optional<T> findById(int id) {
        for (T x : items) {
            if (x.getId() == id) {
                return Optional.of(x);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> findByCode(String code) {
        if (code == null) return Optional.empty();
        String wanted = code.trim();

        for (T x : items) {
            if (x.getCode() != null && x.getCode().equalsIgnoreCase(wanted)) {
                return Optional.of(x);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(items);
    }

    @Override
    public boolean delete(T entity) {
        for(T x : items){
            if(x.getId() == entity.getId()){
                items.remove(x);
                return true;
            }
        }
        return false;
    }
}
