package com.futongware.temperaturemeasuringbracelet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "com.futongware.temperaturemeasuringbracelet")
public class TemperatureMeasuringBraceletApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemperatureMeasuringBraceletApplication.class, args);
    }

}
