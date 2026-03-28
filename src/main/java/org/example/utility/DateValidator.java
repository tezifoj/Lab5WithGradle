package org.example.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateValidator {
    /**
     * Проверяет корректность всех компонентов даты и возвращает список ошибок
     */
    public static List<String> validateDateTimeWithErrors(int year, int month, int day, int hour, int minute, int second) {
        List<String> errors = new ArrayList<>();
        if (year > 2026) {
            errors.add("Год не может быть больше 2026");
        }
        if (year < 1900) {
            errors.add("Год не может быть меньше 1900");
        }

        if (month < 1 || month > 12) {
            errors.add("Месяц должен быть от 1 до 12");
        }

        if (month >= 1 && month <= 12) {
            int maxDay = getDaysInMonth(year, month);
            if (day < 1 || day > maxDay) {
                errors.add("День должен быть от 1 до " + maxDay + " для указанного месяца");
            }
        } else {
            if (day < 1 || day > 31) {
                errors.add("День должен быть от 1 до 31");
            }
        }

        if (hour < 0 || hour > 23) {
            errors.add("Часы должны быть от 0 до 23");
        }

        if (minute < 0 || minute > 59) {
            errors.add("Минуты должны быть от 0 до 59");
        }

        if (second < 0 || second > 59) {
            errors.add("Секунды должны быть от 0 до 59");
        }

        return errors;
    }

    /**
     * Проверяет корректность всех компонентов даты
     * @throws IllegalArgumentException если есть ошибки
     */
    public static void validateDateTime(int year, int month, int day, int hour, int minute, int second) {
        List<String> errors = validateDateTimeWithErrors(year, month, day, hour, minute, second);

        if (!errors.isEmpty()) {
            StringBuilder message = new StringBuilder("Обнаружены ошибки в дате:");
            for (String error : errors) {
                message.append("\n- ").append(error);
            }
            throw new IllegalArgumentException(message.toString());
        }
    }

    /**
     * Проверяет корректность даты без времени
     */
    public static void validateDate(int year, int month, int day) {
        validateDateTime(year, month, day, 0, 0, 0);
    }

    /**
     * Возвращает количество дней в месяце с учетом високосного года
     */
    private static int getDaysInMonth(int year, int month) {
        Calendar calendar = new GregorianCalendar(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Парсит строку с датой и временем и проверяет все компоненты
     */
    public static Date parseAndValidateDateTime(String dateStr, String format) throws ParseException, IllegalArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);

        Date date = sdf.parse(dateStr);

        try {
            if (format.contains("HH:mm:ss")) {
                String[] dateTimeParts = dateStr.split(" ");
                String[] dateParts = dateTimeParts[0].split("-");
                String[] timeParts = dateTimeParts[1].split(":");

                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                int second = Integer.parseInt(timeParts[2]);

                validateDateTime(year, month, day, hour, minute, second);
            } else {
                String[] dateParts = dateStr.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);

                validateDate(year, month, day);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат даты: " + dateStr);
        }

        return date;
    }
}