package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Interfaces.IRequestRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IRequestCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class RequestRepository implements IRequestRepository {
    @Autowired
    private IRequestCrudRepository requestCrudRepository;
}
