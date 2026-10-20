package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Domain.Entities.User;
import com.example.demo.Infrastructure.Persistence.Entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPasswordHash(),
                userEntity.getRole(), userEntity.getStudentId());
    }
}
