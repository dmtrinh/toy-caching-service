package com.thedarkside.toycachingservice.Controller;

import com.thedarkside.toycachingservice.service.ToyCachingService;
import com.thedarkside.toycachingservice.model.IdempotentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest
@DisabledInAotMode
class ToyCachingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ToyCachingService cachingService;

    @Autowired
    private ObjectMapper objectMapper;

    //IdempotentRequest idempotentRequest = IdempotentRequest.builder().build();
    IdempotentRequest idempotentRequest = new IdempotentRequest();

    @BeforeEach
    void setup() {
        idempotentRequest.setRequestDatetime(LocalDateTime.now());
        idempotentRequest.setTtl(60L);
        idempotentRequest.setRequestPayload("requestPayload");
        idempotentRequest.setRequestPayload("responsePayload");
        idempotentRequest.setIdempotencyKey("Order-66");
    }

    @Test
    void test_save_givenRequest_returnSuccess() throws Exception {

        given(cachingService.save(idempotentRequest)).willReturn("Order-66");

        ResultActions response = mockMvc.perform(post("/caching-service/cache")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idempotentRequest)));

        //then
        response.andExpect(status().isOk());
    }

    @Test
    void test_retrieve_givenIdempotencyKey_returnSuccess() throws Exception {

        given(cachingService.retrieve("Order-66")).willReturn(idempotentRequest);

        ResultActions response = mockMvc.perform(get("/caching-service/cache/Order-66")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idempotentRequest)));

        //then
        response.andExpect(status().isOk());
    }

    @Test
    void test_retrieve_givenNullRequest_return404() throws Exception {

        given(cachingService.retrieve("Order-66")).willReturn(null);

        ResultActions response = mockMvc.perform(get("/caching-service/cache/Order-66")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idempotentRequest)));

        //then
        response.andExpect(status().isNotFound());
    }

    // test verify given NULL request return 404
    @Test
    void test_verify_givenNullRequest_return404() throws Exception {

        given(cachingService.retrieve("Order-66")).willReturn(null);
    
        ResultActions response = mockMvc.perform(post("/caching-service/cache/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idempotentRequest)));

        //then
        response.andExpect(status().isNotFound());
    }

    // test verify given request with different payload return 409
    @Test
    void test_verify_givenRequestWithDifferentPayload_return409() throws Exception {

        IdempotentRequest request = new IdempotentRequest();
        request.setRequestPayload("payload");
        request.setIdempotencyKey("Order-66");

        given(cachingService.retrieve("Order-66")).willReturn(idempotentRequest);

        ResultActions response = mockMvc.perform(post("/caching-service/cache/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        response.andExpect(status().isConflict());
    }

    // test verify given request with same payload return 200
    @Test
    void test_verify_givenRequestWithSamePayload_return200() throws Exception {

        given(cachingService.retrieve("Order-66")).willReturn(idempotentRequest);

        ResultActions response = mockMvc.perform(post("/caching-service/cache/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idempotentRequest)));

        //then
        response.andExpect(status().isOk());
    }

    // test get all records return 200
    @Test
    void test_getAllRecords_return200() throws Exception {

        // Mock the getAllRecords method of the cachingService to return list of IdempotentRequests
        given(cachingService.getAllRecords()).willReturn(List.of(new IdempotentRequest()));
        
        ResultActions response = mockMvc.perform(get("/caching-service/cache")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        response.andExpect(status().isOk());
    }

    // test get all records return 404
    @Test
    void test_getAllRecords_return404() throws Exception {

        // Mock the getAllRecords method of the cachingService to return null
        given(cachingService.getAllRecords()).willReturn(null);
        
        ResultActions response = mockMvc.perform(get("/caching-service/cache")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        response.andExpect(status().isNotFound());
    }
}
