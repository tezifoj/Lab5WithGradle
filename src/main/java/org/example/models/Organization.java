package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {
    private Long employeesCount;
    private OrganizationType type;
    private Address officialAddress;

    public Organization() {
    }

    @JsonCreator
    public Organization(
            @JsonProperty("employeesCount") Long employeesCount,
            @JsonProperty("type") OrganizationType type,
            @JsonProperty("officialAddress") Address officialAddress) {
        if (employeesCount == null) {
            throw new IllegalArgumentException("employeesCount не может быть null");
        }
        if (employeesCount <= 0) {
            throw new IllegalArgumentException("employeesCount должно быть положительным");
        }
        if (type == null) {
            throw new IllegalArgumentException("type не может быть null");
        }
        if (officialAddress == null) {
            throw new IllegalArgumentException("officialAddress не может быть null");
        }
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    public Long getEmployeesCount() { return employeesCount; }
    public OrganizationType getType() { return type; }
    public Address getOfficialAddress() { return officialAddress; }

    public void setEmployeesCount(Long employeesCount) { this.employeesCount = employeesCount; }
    public void setType(OrganizationType type) { this.type = type; }
    public void setOfficialAddress(Address officialAddress) { this.officialAddress = officialAddress; }

    @Override
    public String toString() {
        return employeesCount + ";" + type + ";" + officialAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        Organization that = (Organization) o;
        return Objects.equals(employeesCount, that.employeesCount) &&
                type == that.type &&
                Objects.equals(officialAddress, that.officialAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeesCount, type, officialAddress);
    }
}