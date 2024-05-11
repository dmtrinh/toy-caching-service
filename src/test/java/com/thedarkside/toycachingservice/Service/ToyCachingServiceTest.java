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
import java.util.List;
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
    void test_retrieve_givenIdempotentKey_returnNull() {

        // Mock the findById method of the idempotentRequestRepository to return an empty Optional
        given(idempotentRequestRepository.findById("Order-66")).willReturn(Optional.empty());
    
        IdempotentRequest response = cachingService.retrieve("Order-66");
        assertThat(response).isNull();
    }

    @Test
    void test_getAllRecords_returnSuccess() {

        // Mock the findAll method of the idempotentRequestRepository to return list of IdempotentRequest
        given(idempotentRequestRepository.findAll()).willReturn(List.of(idempotentRequest));

        // Assert that the result of calling getAllRecords on the cachingService is all the records in the repository
        assertThat(cachingService.getAllRecords()).isEqualTo(List.of(idempotentRequest));
    }
}
