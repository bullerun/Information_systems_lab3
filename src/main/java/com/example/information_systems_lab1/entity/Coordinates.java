package com.example.information_systems_lab1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin(value = "-737.0", inclusive = false, message = "Значение x должно быть больше -737")
    private double x;


    @NotNull(message = "Поле y не может быть null")
    @Max(value = 870, message = "Значение y должно быть меньше 870")
    private Long y;


}
