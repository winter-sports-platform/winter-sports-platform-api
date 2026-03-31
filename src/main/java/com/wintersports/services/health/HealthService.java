package com.wintersports.services.health;

import com.wintersports.entities.Health;
import com.wintersports.repositories.health.IHealthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthService implements IHealthService {

    private final IHealthRepository healthRepository;

    @Override
    public List<Health> getAll() {
        return healthRepository.findAll();
    }

    @Override
    public Health getById(Long id) {
        return healthRepository.findById(id).orElseThrow();
    }

    @Override
    public Health create(Health entity) {
        return healthRepository.save(entity);
    }

    @Override
    public Health update(Long id, Health entity) {
        entity.setId(id);
        return healthRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        healthRepository.deleteById(id);
    }
}