package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IUserCrudRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
