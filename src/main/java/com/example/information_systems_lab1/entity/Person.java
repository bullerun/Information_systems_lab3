package com.example.information_systems_lab1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "Поле name не может быть null")
    @Size(min = 1, message = "Строка не может быть пустой")
    private String name; //Поле не может быть null, Строка не может быть пустой


    @NotNull(message = "Поле eyeColor не может быть null")
    @Enumerated(EnumType.STRING)
    private Color eyeColor; //Поле не может быть null


    @NotNull(message = "Поле hairColor не может быть null")
    @Enumerated(EnumType.STRING)
    private Color hairColor; //Поле может быть null


    @NotNull(message = "Поле location не может быть null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; //Поле может быть null


    @DecimalMin(value = "0.0", inclusive = false, message = "Значение поля должно быть больше 0")
    private float weight; //Значение поля должно быть больше 0


    @NotNull(message = "Поле nationality не может быть null")
    @Enumerated(EnumType.STRING)
    private Country nationality; //Поле может быть null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, insertable = false, updatable = false)
    private User owner;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (Float.compare(weight, person.weight) != 0) return false;
        if (!name.equals(person.name)) return false;
        if (eyeColor != person.eyeColor) return false;
        if (hairColor != person.hairColor) return false;
        if (!location.equals(person.location)) return false;
        return nationality == person.nationality;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + eyeColor.hashCode();
        result = 31 * result + hairColor.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + (weight != 0.0f ? Float.floatToIntBits(weight) : 0);
        result = 31 * result + nationality.hashCode();
        return result;
    }
}
