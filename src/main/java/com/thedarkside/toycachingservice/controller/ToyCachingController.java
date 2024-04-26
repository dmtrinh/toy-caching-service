package com.thedarkside.toycachingservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.thedarkside.toycachingservice.model.IdempotentRequest;
import com.thedarkside.toycachingservice.service.ToyCachingService;

import lombok.SneakyThrows;

import java.util.List;

/**
 * ToyCachingController
 *
 */
@RestController
@RequestMapping("/caching-service")
public class ToyCachingController {
    @Autowired
    ToyCachingService cachingService;

    private static Logger log = LoggerFactory.getLogger(ToyCachingController.class);

    /**
     * Save to cache
     * 
     * @param request
     * @return
     */
    @SneakyThrows
    @PostMapping(path = "/cache")
    public ResponseEntity<String> save(@RequestBody IdempotentRequest request) {
        log.info("Saving to cache for key::{}", request.getIdempotencyKey());
        log.debug("Request::{}", request);
        return new ResponseEntity<>(cachingService.save(request), HttpStatus.OK);

    }

    @SneakyThrows
    @PostMapping(path = "/cache/verify")
    public ResponseEntity<String> verify(@RequestBody IdempotentRequest request) {
        log.info("Checking cache for key::{}", request.getIdempotencyKey());
        IdempotentRequest cachedRequest = cachingService.retrieve(request.getIdempotencyKey());

        if (cachedRequest != null) {
            if (request.getRequestPayload().equals(cachedRequest.getRequestPayload())) {
                //String json = (new ObjectMapper()).writeValueAsString(idempotentRequest);
                //return new ResponseEntity<>(json, HttpStatus.OK);
                return new ResponseEntity<>(cachedRequest.getResponsePayload(), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieve an item from cache
     * 
     * @param key
     * @return cached payload
     */
    @SneakyThrows
    @GetMapping(path = "/cache/{key}")
    public ResponseEntity<String> retrieve(@PathVariable String key) {
        IdempotentRequest request = cachingService.retrieve(key);
        if (request != null) {
            //String json = (new ObjectMapper()).writeValueAsString(request);
            return new ResponseEntity<>(request.getResponsePayload(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @SneakyThrows
    @GetMapping(path = "/cache")
    public ResponseEntity<List<IdempotentRequest>> getAllRecords() {
        List<IdempotentRequest> request = cachingService.getAllRecords();
        if (request != null && !CollectionUtils.isEmpty(request)) {
            return new ResponseEntity<>(request, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
