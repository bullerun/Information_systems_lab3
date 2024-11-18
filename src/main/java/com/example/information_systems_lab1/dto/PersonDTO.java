package com.example.information_systems_lab1.dto;

import com.example.information_systems_lab1.entity.Color;
import com.example.information_systems_lab1.entity.Country;
import com.example.information_systems_lab1.entity.Location;

import lombok.Data;

@Data
public class PersonDTO {
    private Long id;
    private String name;
    private Color eyeColor;
    private Color hairColor;
    private Location location;
    private float weight;
    private Country nationality;
    private Long owner_id;
}
