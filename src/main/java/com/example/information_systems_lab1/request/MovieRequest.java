package com.example.information_systems_lab1.request;

import com.example.information_systems_lab1.entity.Coordinates;
import com.example.information_systems_lab1.entity.MovieGenre;
import com.example.information_systems_lab1.entity.MpaaRating;
import com.example.information_systems_lab1.entity.Person;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovieRequest {


    @NotNull(message = "Поле name не может быть null")
    @Size(min = 1, message = "Строка не может быть пустой")
    private String name; //Поле не может быть null, Строка не может быть пустой


    @NotNull(message = "Поле coordinates не может быть null")
    @OneToOne
    @JoinColumn(name = "coordinate_id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null


    @Min(value = 1, message = "Значение поля oscarsCount должно быть больше 0")
    private long oscarsCount; //Значение поля должно быть больше 0


    @DecimalMin(value = "0.0", inclusive = false, message = "Значение поля budget должно быть больше 0")
    private double budget; //Значение поля должно быть больше 0

    @Min(value = 1, message = "Значение поля totalBoxOffice должно быть больше 0")
    private Integer totalBoxOffice; //Поле не может быть null, Значение поля должно быть больше 0

    @NotNull(message = "Поле mpaaRating не может быть null")
    @Enumerated(EnumType.STRING)
    private MpaaRating mpaaRating; //Поле не может быть null


    private Person director; //Поле не может быть null


    private Person screenwriter;


    private Person operator; //Поле не может быть null


    @NotNull(message = "Поле length не может быть null")
    @Min(value = 1, message = "Значение поля length должно быть больше 0")
    private Integer length; //Поле не может быть null, Значение поля должно быть больше 0


    @Min(value = 1, message = "Значение поля goldenPalmCount должно быть больше 0")
    private int goldenPalmCount; //Значение поля должно быть больше 0

    @NotNull(message = "Поле genre не может быть null")
    @Enumerated(EnumType.STRING)
    private MovieGenre genre; //Поле может быть null

    private Long director_id;
    private Long screenwriter_id;
    private Long operator_id;


}
