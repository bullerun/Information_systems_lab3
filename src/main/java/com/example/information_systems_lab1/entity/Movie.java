package com.example.information_systems_lab1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически


    @NotNull(message = "Поле name не может быть null")
    @Size(min = 1, message = "Строка не может быть пустой")
    private String name; //Поле не может быть null, Строка не может быть пустой


    @OneToOne
    @JoinColumn(name = "coordinate_id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @NotNull(message = "Поле creationDate не может быть null")
    @CreatedDate
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Min(value = 1, message = "Значение поля oscarsCount должно быть больше 0")
    private long oscarsCount; //Значение поля должно быть больше 0


    @DecimalMin(value = "0.0", inclusive = false, message = "Значение поля budget должно быть больше 0")
    private double budget; //Значение поля должно быть больше 0

    @Min(value = 1, message = "Значение поля totalBoxOffice должно быть больше 0")
    private Integer totalBoxOffice; //Поле не может быть null, Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    private MpaaRating mpaaRating; //Поле не может быть null

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Person director; //Поле не может быть null


    @ManyToOne
    @JoinColumn(name = "screenwriter_id")
    private Person screenwriter;

    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = false)
    private Person operator; //Поле не может быть null


    @NotNull(message = "Поле length не может быть null")
    @Min(value = 1, message = "Значение поля length должно быть больше 0")
    private Integer length; //Поле не может быть null, Значение поля должно быть больше 0


    @Min(value = 1, message = "Значение поля length должно быть больше 0")
    private int goldenPalmCount; //Значение поля должно быть больше 0

    @NotNull(message = "Поле genre не может быть null")
    @Enumerated(EnumType.STRING)
    private MovieGenre genre; //Поле может быть null
}