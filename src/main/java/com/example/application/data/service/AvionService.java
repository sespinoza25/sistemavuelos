package com.example.application.data.service;

import com.example.application.data.entity.Avion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AvionService {

    private AvionRepository repository;

    public AvionService(@Autowired AvionRepository repository) {
        this.repository = repository;
    }

    public Optional<Avion> get(UUID id) {
        return repository.findById(id);
    }

    public Avion update(Avion entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Avion> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
