package org.example.models;

import org.example.utility.Console;
import org.example.utility.DateValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.NoSuchElementException;

public class Ask {
    public static class AskBreak extends Exception {
    }

    public static Worker askWorker(Console console, int id) throws AskBreak {
        try {
            String name;
            while (true) {
                console.print("name: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: имя не может быть пустым");
                    continue;
                }
                if (line.length() > 255) {
                    console.println("Ошибка: имя слишком длинное (максимум 255 символов)");
                    continue;
                }
                name = line;
                break;
            }

            Coordinates coordinates = askCoordinates(console);
            if (coordinates == null) throw new AskBreak();

            Double salary;
            while (true) {
                console.print("salary: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    salary = null;
                    break;
                }

                if (!line.matches("-?\\d+(\\.\\d+)?")) {
                    console.println("Ошибка: salary должно быть числом (пример: 50000 или 50000.50)");
                    continue;
                }

                try {
                    salary = Double.parseDouble(line);
                    if (salary <= 0) {
                        console.println("Ошибка: salary должен быть больше 0");
                        continue;
                    }
                    if (salary > 1_000_000_000) {
                        console.println("Ошибка: salary слишком большой (максимум 1 миллиард)");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    console.println("Ошибка: введите корректное число");
                }
            }

            Date startDate;
            while (true) {
                console.print("startDate (форматы: ГГГГ-ММ-ДД или ГГГГ-ММ-ДД ЧЧ:ММ:СС): ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: startDate не может быть пустым");
                    continue;
                }

                try {
                    Date parsedDate = null;
                    boolean parsed = false;
                    String[] formats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};

                    for (String format : formats) {
                        try {
                            parsedDate = DateValidator.parseAndValidateDateTime(line, format);
                            parsed = true;
                            break;
                        } catch (ParseException e) {
                        } catch (IllegalArgumentException e) {
                            console.println("Ошибка: " + e.getMessage());
                            parsed = false;
                            break;
                        }
                    }

                    if (!parsed && line.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                        try {
                            String[] parts = line.split("\\.");
                            String reformatted = parts[2] + "-" + parts[1] + "-" + parts[0];
                            parsedDate = DateValidator.parseAndValidateDateTime(reformatted, "yyyy-MM-dd");
                            parsed = true;
                        } catch (IllegalArgumentException e) {
                            console.println("Ошибка: " + e.getMessage());
                            continue;
                        } catch (ParseException e) {
                        }
                    }

                    if (!parsed) {
                        console.println("Неверный формат! Используйте:");
                        console.println("  ГГГГ-ММ-ДД (например: 2025-03-15)");
                        console.println("  ГГГГ-ММ-ДД ЧЧ:ММ:СС (например: 2025-03-15 14:30:00)");
                        console.println("  ДД.ММ.ГГГГ (например: 15.03.2025)");
                        continue;
                    }

                    Date currentDate = new Date();
                    if (parsedDate.after(currentDate)) {
                        console.println("Ошибка: дата не может быть в будущем");
                        continue;
                    }

                    startDate = parsedDate;
                    console.println("Дата принята: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(startDate));
                    break;

                } catch (IllegalArgumentException e) {
                    console.println("Ошибка: " + e.getMessage());
                }
            }

            LocalDateTime endDate = null;
            while (true) {
                console.print("endDate (форматы: ГГГГ-ММ-ДД или ГГГГ-ММ-ДДTЧЧ:ММ:СС, пустая строка = null): ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("endDate не указана (null)");
                    break;
                }

                try {
                    int year, month, day, hour = 0, minute = 0, second = 0;

                    if (line.contains("T")) {
                        String[] dateTimeParts = line.split("T");
                        if (dateTimeParts.length != 2) {
                            console.println("Ошибка: неверный формат. Используйте ГГГГ-ММ-ДДTЧЧ:ММ:СС");
                            continue;
                        }

                        String[] dateParts = dateTimeParts[0].split("-");
                        String[] timeParts = dateTimeParts[1].split(":");

                        if (dateParts.length != 3 || timeParts.length < 2) {
                            console.println("Ошибка: неверный формат даты или времени");
                            continue;
                        }

                        year = Integer.parseInt(dateParts[0]);
                        month = Integer.parseInt(dateParts[1]);
                        day = Integer.parseInt(dateParts[2]);
                        hour = Integer.parseInt(timeParts[0]);
                        minute = Integer.parseInt(timeParts[1]);
                        second = timeParts.length > 2 ? Integer.parseInt(timeParts[2]) : 0;
                    } else {
                        String[] dateParts = line.split("-");
                        if (dateParts.length != 3) {
                            console.println("Ошибка: неверный формат даты. Используйте ГГГГ-ММ-ДД");
                            continue;
                        }

                        year = Integer.parseInt(dateParts[0]);
                        month = Integer.parseInt(dateParts[1]);
                        day = Integer.parseInt(dateParts[2]);
                    }

                    try {
                        DateValidator.validateDateTime(year, month, day, hour, minute, second);
                    } catch (IllegalArgumentException e) {
                        console.println("Ошибка: " + e.getMessage());
                        continue;
                    }

                    LocalDateTime parsedEndDate = LocalDateTime.of(year, month, day, hour, minute, second);
                    if (parsedEndDate.isAfter(LocalDateTime.now())) {
                        console.println("Ошибка: дата окончания не может быть в будущем");
                        continue;
                    }

                    endDate = parsedEndDate;
                    console.println("Дата принята: " + endDate.format(DateTimeFormatter.ISO_DATE_TIME));
                    break;

                } catch (NumberFormatException e) {
                    console.println("Ошибка: введите числа, а не текст");
                } catch (IllegalArgumentException e) {
                    console.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    console.println("Ошибка: неверный формат даты");
                }
            }

            Organization organization = askOrganization(console);
            Status status = askStatus(console);
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
        try {
            Float x = null;
            while (true) {
                console.print("coordinates.x: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: coordinates.x не может быть пустым");
                    continue;
                }

                if (!line.matches("-?\\d+(\\.\\d+)?")) {
                    console.println("Ошибка: введите число");
                    continue;
                }

                try {
                    x = Float.parseFloat(line);
                    if (x <= -912) {
                        console.println("Ошибка: x должен быть больше -912");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    console.println("Ошибка: введите корректное число");
                }
            }

            Double y = null;
            while (true) {
                console.print("coordinates.y: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: coordinates.y не может быть пустым");
                    continue;
                }

                if (!line.matches("-?\\d+(\\.\\d+)?")) {
                    console.println("Ошибка: введите число");
                    continue;
                }

                try {
                    y = Double.parseDouble(line);
                    if (y > 139) {
                        console.println("Ошибка: y должен быть не больше 139");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    console.println("Ошибка: введите корректное число");
                }
            }

            return new Coordinates(x, y);

        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения координат");
            return null;
        }
    }

    public static Organization askOrganization(Console console) throws AskBreak {
        try {
            Long employeesCount;
            while (true) {
                console.print("organization.employeesCount: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: employeesCount не может быть пустым");
                    continue;
                }

                if (!line.matches("\\d+")) {
                    console.println("Ошибка: введите целое положительное число");
                    continue;
                }

                try {
                    employeesCount = Long.parseLong(line);
                    if (employeesCount <= 0) {
                        console.println("Ошибка: employeesCount должно быть больше 0");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    console.println("Ошибка: введите корректное число");
                }
            }

            OrganizationType type = askOrganizationType(console);
            if (type == null) throw new AskBreak();

            Address officialAddress = askAddress(console);
            if (officialAddress == null) throw new AskBreak();

            return new Organization(employeesCount, type, officialAddress);

        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения организации");
            return null;
        }
    }

    public static Address askAddress(Console console) throws AskBreak {
        try {
            String street;
            while (true) {
                console.print("street: ");
                street = console.readln().trim();
                if (street.equals("exit")) throw new AskBreak();

                if (street.isEmpty()) {
                    console.println("Ошибка: street не может быть пустым");
                    continue;
                }
                if (street.length() > 193) {
                    console.println("Ошибка: длина street не должна превышать 193 символа");
                    continue;
                }
                break;
            }

            String zipCode;
            while (true) {
                console.print("zipCode (пустая строка = null): ");
                zipCode = console.readln().trim();
                if (zipCode.equals("exit")) throw new AskBreak();

                if (zipCode.isEmpty()) {
                    zipCode = null;
                    break;
                }
                if (zipCode.length() > 18) {
                    console.println("Ошибка: длина zipCode не должна превышать 18 символов");
                    continue;
                }
                if (!zipCode.matches("[A-Za-z0-9\\-]+")) {
                    console.println("Ошибка: zipCode может содержать только буквы, цифры и дефис");
                    continue;
                }
                break;
            }

            return new Address(street, zipCode);

        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения адреса");
            return null;
        }
    }

    public static Status askStatus(Console console) throws AskBreak {
        try {
            while (true) {
                console.print("Status (" + Status.names() + "): ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: status не может быть пустым");
                    continue;
                }

                try {
                    return Status.valueOf(line.toUpperCase());
                } catch (IllegalArgumentException e) {
                    console.println("Ошибка: статус '" + line + "' не существует");
                    console.println("Доступные статусы: " + Status.names());
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения статуса");
            return null;
        }
    }

    public static OrganizationType askOrganizationType(Console console) throws AskBreak {
        try {
            while (true) {
                console.print("OrganizationType (" + OrganizationType.names() + "): ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();

                if (line.isEmpty()) {
                    console.println("Ошибка: тип организации не может быть пустым");
                    continue;
                }

                try {
                    return OrganizationType.valueOf(line.toUpperCase());
                } catch (IllegalArgumentException e) {
                    console.println("Ошибка: тип '" + line + "' не существует");
                    console.println("Доступные типы: " + OrganizationType.names());
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения типа организации");
            return null;
        }
    }
}