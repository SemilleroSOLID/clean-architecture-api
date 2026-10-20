package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.RequestEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IRequestCrudRepository extends CrudRepository<RequestEntity, Integer> {
    boolean existsByConvocationId(Integer convocationId);
    boolean existsByConvocationIdAndRequestStateIn(Integer convocationId, Collection<Integer> requestStates);
    boolean existsByStudentIdAndConvocationId(Integer studentId, Integer convocationId);

    @Query("SELECT r FROM RequestEntity r"
            + " WHERE (:convocationId IS NULL OR r.convocationId = :convocationId)"
            + " AND (:requestState IS NULL OR r.requestState = :requestState)"
            + " ORDER BY r.creationDate DESC, r.id DESC")
    List<RequestEntity> findByFilters(@Param("convocationId") Integer convocationId, @Param("requestState") Integer requestState);
}
