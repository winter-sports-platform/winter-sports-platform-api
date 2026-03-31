package com.wintersports.services.health;

import com.wintersports.entities.Health;
import com.wintersports.repositories.health.IHealthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthServiceTest {

    @Mock
    private IHealthRepository healthRepository;

    @InjectMocks
    private HealthService healthService;

    @Test
    void getAll_shouldReturnAllRecords() {
        Health h = new Health();
        h.setStatus("OK");

        when(healthRepository.findAll()).thenReturn(List.of(h));

        List<Health> result = healthService.getAll();

        assertEquals(1, result.size());
        assertEquals("OK", result.getFirst().getStatus());
        verify(healthRepository, times(1)).findAll();
    }
}