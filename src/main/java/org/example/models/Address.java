package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private String street;
    private String zipCode;

    public Address() {
    }

    @JsonCreator
    public Address(
            @JsonProperty("street") String street,
            @JsonProperty("zipCode") String zipCode) {
        if (street == null) {
            throw new IllegalArgumentException("street не может быть null");
        }
        if (street.length() > 193) {
            throw new IllegalArgumentException("Длина street не должна превышать 193 символа");
        }
        if (street.isEmpty()) {
            throw new IllegalArgumentException("street не может быть пустым");
        }

        if (zipCode != null && zipCode.length() > 18) {
            throw new IllegalArgumentException("Длина zipCode не должна превышать 18 символов");
        }

        this.street = street;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return street + ";" + (zipCode != null ? zipCode : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return street.equals(address.street) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, zipCode);
    }
}