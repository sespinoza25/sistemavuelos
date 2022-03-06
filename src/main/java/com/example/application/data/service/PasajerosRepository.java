package com.example.application.data.service;

import com.example.application.data.entity.Pasajeros;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasajerosRepository extends JpaRepository<Pasajeros, UUID> {

}