package com.example.is_backend.dto;

import com.example.is_backend.entity.Color;
import com.example.is_backend.entity.Country;
import com.example.is_backend.entity.Location;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonDTO {
    private Long id;


    @NotNull(message = "Поле name не может быть null")
    @Size(min = 1, message = "Строка не может быть пустой")
    private String name;


    @NotNull(message = "Поле eyeColor не может быть null")
    @Enumerated(EnumType.STRING)
    private Color eyeColor;


    @NotNull(message = "Поле hairColor не может быть null")
    @Enumerated(EnumType.STRING)
    private Color hairColor;


    private Location location;


    @DecimalMin(value = "0.0", inclusive = false, message = "Значение поля должно быть больше 0")
    private float weight;

    @NotNull(message = "Поле nationality не может быть null")
    @Enumerated(EnumType.STRING)
    private Country nationality;


    private Long owner_id;
}
