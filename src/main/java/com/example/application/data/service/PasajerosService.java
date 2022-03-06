package com.example.application.data.service;

import com.example.application.data.entity.Pasajeros;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PasajerosService {

    private PasajerosRepository repository;

    public PasajerosService(@Autowired PasajerosRepository repository) {
        this.repository = repository;
    }

    public Optional<Pasajeros> get(UUID id) {
        return repository.findById(id);
    }

    public Pasajeros update(Pasajeros entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Pasajeros> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
