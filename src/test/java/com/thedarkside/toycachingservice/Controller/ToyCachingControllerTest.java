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
}
