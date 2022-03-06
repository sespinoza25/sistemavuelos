package com.example.application.data.service;

import com.example.application.data.entity.Ruta;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, UUID> {

}