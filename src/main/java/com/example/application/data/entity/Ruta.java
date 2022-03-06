package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import java.util.UUID;
import javax.persistence.Entity;

@Entity
public class Ruta extends AbstractEntity {

    private UUID id_ruta;
    private String destino;
    private Integer valor_pasaje;

    public UUID getId_ruta() {
        return id_ruta;
    }
    public void setId_ruta(UUID id_ruta) {
        this.id_ruta = id_ruta;
    }
    public String getDestino() {
        return destino;
    }
    public void setDestino(String destino) {
        this.destino = destino;
    }
    public Integer getValor_pasaje() {
        return valor_pasaje;
    }
    public void setValor_pasaje(Integer valor_pasaje) {
        this.valor_pasaje = valor_pasaje;
    }

}
