package com.example.application.data.service;

import com.example.application.data.entity.Avion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvionRepository extends JpaRepository<Avion, UUID> {

}