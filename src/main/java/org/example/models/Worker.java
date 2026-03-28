package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.utility.Element;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Worker extends Element {
    private Long id;
    private String name;
    private Coordinates coordinates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    private Double salary;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private Status status;
    private Organization organization;

    // 1. ПУСТОЙ КОНСТРУКТОР (обязательно!)
    public Worker() {
    }

    // 2. КОНСТРУКТОР С АННОТАЦИЯМИ ДЛЯ JSON
    @JsonCreator
    public Worker(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("coordinates") Coordinates coordinates,
            @JsonProperty("creationDate") Date creationDate,
            @JsonProperty("salary") Double salary,
            @JsonProperty("startDate") Date startDate,
            @JsonProperty("endDate") LocalDateTime endDate,
            @JsonProperty("status") Status status,
            @JsonProperty("organization") Organization organization) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.organization = organization;
    }

    // 3. ОСНОВНОЙ КОНСТРУКТОР ДЛЯ ПРОГРАММЫ
    public Worker(long id, String name, Coordinates coordinates, Double salary,
                  Date startDate, LocalDateTime endDate, Status status, Organization organization) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date();
        this.salary = salary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.organization = organization;
    }

    // ГЕТТЕРЫ (обязательно!)
    @Override
    public Long getId() { return id; }

    public String getName() { return name; }

    public Coordinates getCoordinates() { return coordinates; }

    public Date getCreationDate() { return creationDate; }

    public Double getSalary() { return salary; }

    public Date getStartDate() { return startDate; }

    public LocalDateTime getEndDate() { return endDate; }

    public Status getStatus() { return status; }

    public Organization getOrganization() { return organization; }

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public void setSalary(Double salary) { this.salary = salary; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public void setStatus(Status status) { this.status = status; }

    public void setOrganization(Organization organization) { this.organization = organization; }

    @Override
    public int compareTo(Element element) {
        Worker other = (Worker) element;
        if (this.salary == null && other.salary == null) return 0;
        if (this.salary == null) return -1;
        if (other.salary == null) return 1;
        return Double.compare(this.salary, other.salary);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(id, worker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации Worker в JSON", e);
        }
    }
}