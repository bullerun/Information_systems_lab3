package com.example.information_systems_lab1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long x;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (x != location.x) return false;
        if (Float.compare(z, location.z) != 0) return false;
        if (!y.equals(location.y)) return false;
        return name.equals(location.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (x ^ (x >>> 32));
        result = 31 * result + y.hashCode();
        result = 31 * result + (z != 0.0f ? Float.floatToIntBits(z) : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

    @NotNull(message = "Поле y не может быть null")
    private Float y;

    private float z;

    @NotNull(message = "Поле name не может быть null")
    @Size(max = 470, message = "Длина строки name не должна превышать 470 символов")
    private String name;
}
