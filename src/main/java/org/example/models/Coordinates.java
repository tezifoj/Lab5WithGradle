package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinates {
    private float x;
    private double y;

    public Coordinates() {
    }

    @JsonCreator
    public Coordinates(
            @JsonProperty("x") float x,
            @JsonProperty("y") double y) {
        if (x <= -912) {
            throw new IllegalArgumentException("x должен быть больше -912");
        }
        if (y > 139) {
            throw new IllegalArgumentException("y должен быть не больше 139");
        }
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public double getY() { return y; }

    public void setX(float x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() {
        return x + ";" + y;
    }
}