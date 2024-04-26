package com.thedarkside.toycachingservice.model;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Data;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Data
//@Builder
//@Jacksonized
@RedisHash("IdempotentRequest")
public class IdempotentRequest {

    @Id
    private String idempotencyKey;
    private String clientId;
    private String requestMethod;  
    private String urlPath;  
    private LocalDateTime requestDatetime;
    private String requestPayload;
    private String responsePayload;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;
}