package com.wintersports.services;

import java.util.List;

public interface IBaseService<T, ID> {
    List<T> getAll();

    T getById(ID id);

    T create(T entity);

    T update(ID id, T entity);

    void delete(ID id);
}