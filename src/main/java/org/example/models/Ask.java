package org.example.models;

import org.example.utility.Console;
import org.example.utility.DateValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Ask {
    public static class AskBreak extends Exception {
    }

    private static <T> T askWithValidation(Console console, String prompt,
                                           Function<String, T> parser,
                                           Predicate<T> validator,
                                           String errorMessage,
                                           boolean allowEmpty) throws AskBreak {
        boolean valid = false;
        T result = null;

        while (!valid) {
            console.print(prompt);
            String line = console.readln();

            if (line == null) {
                return null;
            }

            line = line.trim();
            if (line.equals("exit")) throw new AskBreak();

            if (line.isEmpty() && allowEmpty) {
                return null;
            }

            if (line.isEmpty()) {
                console.println("Ошибка: значение не может быть пустым");
                continue;
            }

            try {
                T value = parser.apply(line);
                if (validator != null && !validator.test(value)) {
                    console.println(errorMessage);
                    continue;
                }
                result = value;
                valid = true;
            } catch (Exception e) {
                console.println("Ошибка: " + e.getMessage());
            }
        }
        return result;
    }

    private static String askString(Console console, String prompt,
                                    Predicate<String> validator,
                                    String errorMessage,
                                    boolean allowEmpty) throws AskBreak {
        return askWithValidation(console, prompt, s -> s, validator, errorMessage, allowEmpty);
    }

    private static <T extends Number> T askNumber(Console console, String prompt,
                                                  Function<String, T> parser,
                                                  Predicate<T> validator,
                                                  String errorMessage,
                                                  boolean allowEmpty) throws AskBreak {
        return askWithValidation(console, prompt, parser, validator, errorMessage, allowEmpty);
    }

    private static <T extends Enum<T>> T askEnum(Console console, String prompt,
                                                 Class<T> enumClass,
                                                 boolean allowEmpty) throws AskBreak {
        return askWithValidation(console, prompt + " (" + getEnumNames(enumClass) + "): ",
                s -> {
                    try {
                        return Enum.valueOf(enumClass, s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Значение должно быть одним из: " + getEnumNames(enumClass));
                    }
                },
                null, "", allowEmpty);
    }

    private static <T extends Enum<T>> String getEnumNames(Class<T> enumClass) {
        StringBuilder names = new StringBuilder();
        for (T value : enumClass.getEnumConstants()) {
            names.append(value.name()).append(", ");
        }
        return names.substring(0, names.length() - 2);
    }

    public static Worker askWorker(Console console, int id) throws AskBreak {
        try {
            String name = askString(console, "name: ",
                    s -> !s.isEmpty() && s.length() <= 255,
                    "Ошибка: имя должно быть от 1 до 255 символов", false);
            if (name == null) throw new AskBreak();

            Coordinates coordinates = askCoordinates(console);
            if (coordinates == null) throw new AskBreak();

            Double salary = askNumber(console, "salary: ", Double::parseDouble,
                    s -> s == null || (s > 0 && s <= 1_000_000_000),
                    "Ошибка: salary должен быть > 0 и ≤ 1_000_000_000", true);

            Date startDate = askStartDate(console);
            if (startDate == null) throw new AskBreak();

            LocalDateTime endDate = askEndDate(console);

            Organization organization = askOrganization(console);
            if (organization == null) throw new AskBreak();

            Status status = askEnum(console, "Status", Status.class, false);
            if (status == null) throw new AskBreak();

            return new Worker(id, name, coordinates, salary, startDate, endDate, status, organization);

        } catch (NoSuchElementException e) {
            console.printError("Ошибка чтения ввода");
            return null;
        } catch (IllegalArgumentException e) {
            console.printError("Ошибка валидации: " + e.getMessage());
            return null;
        }
    }

    public static Coordinates askCoordinates(Console console) throws AskBreak {
        Float x = askNumber(console, "coordinates.x: ", Float::parseFloat,
                val -> val > -912, "Ошибка: x должен быть больше -912", false);
        if (x == null) return null;

        Double y = askNumber(console, "coordinates.y: ", Double::parseDouble,
                val -> val <= 139, "Ошибка: y должен быть не больше 139", false);
        if (y == null) return null;

        return new Coordinates(x, y);
    }

    private static Date askStartDate(Console console) throws AskBreak {
        return askWithValidation(console,
                "startDate (форматы: ГГГГ-ММ-ДД или ГГГГ-ММ-ДД ЧЧ:ММ:СС): ",
                line -> parseDate(line, console),
                date -> !date.after(new Date()),
                "Ошибка: дата не может быть в будущем", false);
    }

    private static Date parseDate(String line, Console console) {
        String[] formats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};

        for (String format : formats) {
            try {
                Date date = DateValidator.parseAndValidateDateTime(line, format);
                console.println("Дата принята: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date));
                return date;
            } catch (ParseException e) {
            } catch (IllegalArgumentException e) {
                console.println("Ошибка: " + e.getMessage());
                return null;
            }
        }

        if (line.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            try {
                String[] parts = line.split("\\.");
                String reformatted = parts[2] + "-" + parts[1] + "-" + parts[0];
                Date date = DateValidator.parseAndValidateDateTime(reformatted, "yyyy-MM-dd");
                console.println("Дата принята: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date));
                return date;
            } catch (Exception e) {
                console.println("Ошибка: " + e.getMessage());
            }
        }

        console.println("Неверный формат! Используйте:");
        console.println("  ГГГГ-ММ-ДД (например: 2025-03-15)");
        console.println("  ГГГГ-ММ-ДД ЧЧ:ММ:СС (например: 2025-03-15 14:30:00)");
        console.println("  ДД.ММ.ГГГГ (например: 15.03.2025)");
        return null;
    }

    private static LocalDateTime askEndDate(Console console) throws AskBreak {
        return askWithValidation(console,
                "endDate (форматы: ГГГГ-ММ-ДД или ГГГГ-ММ-ДДTЧЧ:ММ:СС, пустая строка = null): ",
                line -> parseEndDate(line, console),
                date -> date == null || !date.isAfter(LocalDateTime.now()),
                "Ошибка: дата окончания не может быть в будущем", true);
    }

    private static LocalDateTime parseEndDate(String line, Console console) {
        try {
            if (line.contains("T")) {
                return LocalDateTime.parse(line, DateTimeFormatter.ISO_DATE_TIME);
            } else {
                return LocalDateTime.parse(line + "T00:00:00", DateTimeFormatter.ISO_DATE_TIME);
            }
        } catch (Exception e) {
            console.println("Ошибка: неверный формат даты. Используйте ГГГГ-ММ-ДД или ГГГГ-ММ-ДДTЧЧ:ММ:СС");
            return null;
        }
    }

    public static Organization askOrganization(Console console) throws AskBreak {
        Long employeesCount = askNumber(console, "organization.employeesCount: ", Long::parseLong,
                val -> val > 0, "Ошибка: employeesCount должно быть больше 0", false);
        if (employeesCount == null) return null;

        OrganizationType type = askEnum(console, "OrganizationType", OrganizationType.class, false);
        if (type == null) return null;

        Address officialAddress = askAddress(console);
        if (officialAddress == null) return null;

        return new Organization(employeesCount, type, officialAddress);
    }

    public static Address askAddress(Console console) throws AskBreak {
        String street = askString(console, "street: ",
                s -> !s.isEmpty() && s.length() <= 193,
                "Ошибка: street должна быть от 1 до 193 символов", false);
        if (street == null) return null;

        String zipCode = askString(console, "zipCode (пустая строка = null): ",
                s -> s == null || (s.length() <= 18 && s.matches("[A-Za-z0-9\\-]*")),
                "Ошибка: zipCode может содержать только буквы, цифры и дефис (макс. 18 символов)", true);

        return new Address(street, zipCode);
    }
}