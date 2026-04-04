package com.wintersports.services;

import java.util.List;

public interface IBaseService<TResponse, TCreateRequest, TUpdateRequest, ID> {
    List<TResponse> getAll();
    TResponse getById(ID id);
    TResponse create(TCreateRequest request);
    TResponse update(ID id, TUpdateRequest request);
    void delete(ID id);
}