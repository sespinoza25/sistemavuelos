package com.example.application.data.service;

import com.example.application.data.entity.Ruta;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RutaService {

    private RutaRepository repository;

    public RutaService(@Autowired RutaRepository repository) {
        this.repository = repository;
    }

    public Optional<Ruta> get(UUID id) {
        return repository.findById(id);
    }

    public Ruta update(Ruta entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Ruta> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
