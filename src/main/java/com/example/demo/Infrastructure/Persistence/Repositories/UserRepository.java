package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.User;
import com.example.demo.Domain.Interfaces.IUserRepository;
import com.example.demo.Infrastructure.Mappers.UserPersistenceMapper;
import com.example.demo.Infrastructure.Persistence.Cruds.IUserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {
    @Autowired
    private IUserCrudRepository userCrudRepository;
    @Autowired
    private UserPersistenceMapper userPersistenceMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userCrudRepository.findByEmailIgnoreCase(email).map(this.userPersistenceMapper::toDomain);
    }
}
