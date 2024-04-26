package com.thedarkside.toycachingservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.thedarkside.toycachingservice.model.IdempotentRequest;
import com.thedarkside.toycachingservice.repository.IdempotentRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ToyCachingService {
    @Autowired
    IdempotentRequestRepository idempotentRequestRepository;
    
    @Value("${spring.data.redis.my.cache-ttl-seconds}")
    long redisCacheTtl;

    /** Save request to cache
     * @param request
     * @return key associated with the request
     */
    public String save(IdempotentRequest request) {
        
        if (ObjectUtils.isEmpty(request.getRequestDatetime())){
            request.setRequestDatetime(LocalDateTime.now());
        }
        
        if ((null == request.getTtl()) || (request.getTtl() == 0)){
            request.setTtl(redisCacheTtl);
        }
        
        log.info("Yeah, saving to cache for key::{}",request.getIdempotencyKey());
        return idempotentRequestRepository.save(request).getIdempotencyKey();
    }


    /** Retrieve from cache using key
     * @param key
     * @return IdempotentRequest
     */
    public IdempotentRequest retrieve(String key) {
        IdempotentRequest request = null;
        
        try {
            request =  idempotentRequestRepository.findById(key).get();
            log.info("Zzzz, getting from cache {} with response::{}", key, request);
        } catch(Exception e) {
            log.warn("Oops! Key not found in cache. {}, {}", key, e.getMessage());
        }
        
        return request;
    }

    public List<IdempotentRequest> getAllRecords() {

        return (List<IdempotentRequest>) idempotentRequestRepository.findAll();
    }
}

