package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import java.util.UUID;
import javax.persistence.Entity;

@Entity
public class Avion extends AbstractEntity {

    private UUID id_avion;
    private String descripcion;
    private Integer capacidad;

    public UUID getId_avion() {
        return id_avion;
    }
    public void setId_avion(UUID id_avion) {
        this.id_avion = id_avion;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Integer getCapacidad() {
        return capacidad;
    }
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

}
