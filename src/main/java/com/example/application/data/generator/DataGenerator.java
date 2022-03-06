package com.example.application.data.generator;

import com.example.application.data.entity.Avion;
import com.example.application.data.entity.Pasajeros;
import com.example.application.data.entity.Ruta;
import com.example.application.data.service.AvionRepository;
import com.example.application.data.service.PasajerosRepository;
import com.example.application.data.service.RutaRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasajerosRepository pasajerosRepository, AvionRepository avionRepository,
            RutaRepository rutaRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (pasajerosRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Pasajeros entities...");
            ExampleDataGenerator<Pasajeros> pasajerosRepositoryGenerator = new ExampleDataGenerator<>(Pasajeros.class,
                    LocalDateTime.of(2022, 3, 6, 0, 0, 0));
            pasajerosRepositoryGenerator.setData(Pasajeros::setId_Pasajero, DataType.UUID);
            pasajerosRepositoryGenerator.setData(Pasajeros::setNombre, DataType.WORD);
            pasajerosRepositoryGenerator.setData(Pasajeros::setApellido, DataType.WORD);
            pasajerosRepositoryGenerator.setData(Pasajeros::setDireccion, DataType.ADDRESS);
            pasajerosRepositoryGenerator.setData(Pasajeros::setTelefono, DataType.NUMBER_UP_TO_10);
            pasajerosRepository.saveAll(pasajerosRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Avion entities...");
            ExampleDataGenerator<Avion> avionRepositoryGenerator = new ExampleDataGenerator<>(Avion.class,
                    LocalDateTime.of(2022, 3, 6, 0, 0, 0));
            avionRepositoryGenerator.setData(Avion::setId_avion, DataType.UUID);
            avionRepositoryGenerator.setData(Avion::setDescripcion, DataType.WORD);
            avionRepositoryGenerator.setData(Avion::setCapacidad, DataType.NUMBER_UP_TO_100);
            avionRepository.saveAll(avionRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Ruta entities...");
            ExampleDataGenerator<Ruta> rutaRepositoryGenerator = new ExampleDataGenerator<>(Ruta.class,
                    LocalDateTime.of(2022, 3, 6, 0, 0, 0));
            rutaRepositoryGenerator.setData(Ruta::setId_ruta, DataType.UUID);
            rutaRepositoryGenerator.setData(Ruta::setDestino, DataType.COUNTRY);
            rutaRepositoryGenerator.setData(Ruta::setValor_pasaje, DataType.NUMBER_UP_TO_1000);
            rutaRepository.saveAll(rutaRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}