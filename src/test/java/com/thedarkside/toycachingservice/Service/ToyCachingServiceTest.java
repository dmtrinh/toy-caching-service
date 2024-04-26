package com.thedarkside.toycachingservice.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thedarkside.toycachingservice.model.IdempotentRequest;
import com.thedarkside.toycachingservice.repository.IdempotentRequestRepository;
import com.thedarkside.toycachingservice.service.ToyCachingService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ToyCachingServiceTest {

    @InjectMocks
    ToyCachingService cachingService;

    @Mock
    IdempotentRequestRepository idempotentRequestRepository;

    IdempotentRequest idempotentRequest = new IdempotentRequest();

    @BeforeEach
    void setup() {

        idempotentRequest.setRequestDatetime(LocalDateTime.now());
        idempotentRequest.setTtl(60L);
        idempotentRequest.setIdempotencyKey("Order-66");
    }

    @Test
    void test_save_givenRequest_returnSuccess() {

        given(idempotentRequestRepository.save(idempotentRequest)).willReturn(idempotentRequest);

        String response = cachingService.save(idempotentRequest);
        assertThat(response).isNotNull();
    }

    @Test
    void test_save_givenDateNull_returnSuccess() {

        idempotentRequest.setRequestDatetime(null);

        given(idempotentRequestRepository.save(idempotentRequest)).willReturn(idempotentRequest);

        String response = cachingService.save(idempotentRequest);
        assertThat(response).isNotNull();
    }

    @Test
    void test_save_givenNullTtl_returnSuccess() {

        idempotentRequest.setTtl(null);

        given(idempotentRequestRepository.save(idempotentRequest)).willReturn(idempotentRequest);

        String response = cachingService.save(idempotentRequest);
        assertThat(response).isNotNull();
    }

    @Test
    void test_retrieve_givenIdempotentKey_returnSuccess() {

        given(idempotentRequestRepository.findById("Order-66")).willReturn(Optional.of(idempotentRequest));

        IdempotentRequest response = cachingService.retrieve("Order-66");
        assertThat(response).isNotNull();
    }

    @Test
    void test_retrieve_givenIdempotentKey_returnException() {

        given(idempotentRequestRepository.findById(null)).willReturn(Optional.of(idempotentRequest));

        IdempotentRequest response = cachingService.retrieve("Order-66");
        assertThat(response).isNull();
    }
}
