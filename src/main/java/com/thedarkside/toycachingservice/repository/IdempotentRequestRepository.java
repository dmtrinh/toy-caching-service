package com.thedarkside.toycachingservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.thedarkside.toycachingservice.model.IdempotentRequest;

@Repository
public interface IdempotentRequestRepository extends CrudRepository<IdempotentRequest, String> {}
