package org.example.workers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.models.Worker;
import org.example.utility.Console;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DumpWorker {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    private final String fileName;
    private final Console console;
    private final WorkerValidator validator;

    public DumpWorker(String fileName, Console console) {
        this.fileName = fileName;
        this.console = console;
        this.validator = new WorkerValidator();
    }

    public void writeCollection(Vector<Worker> collection) {
        if (collection == null) {
            console.printError("Ошибка: коллекция null, сохранение отменено");
            return;
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            console.printError("Ошибка: имя файла не указано");
            return;
        }

        WorkerValidator.CollectionValidationResult validationResult =
                validator.validateCollection(collection);

        if (validationResult.hasErrors()) {
            console.printError("Обнаружены ошибки в данных. Сохранение отменено:");
            for (String error : validationResult.getErrors()) {
                console.printError("  " + error);
            }
            return;
        }

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            String json = mapper.writeValueAsString(collection);
            fileWriter.write(json);
            fileWriter.flush();
            console.println("Коллекция Worker успешно сохранена в файл!");
        } catch (IOException exception) {
            console.printError("Ошибка при сохранении файла: " + exception.getMessage());
        }
    }

    public void readCollection(Vector<Worker> collection) {
        if (fileName == null || fileName.isEmpty()) {
            console.printError("Аргумент командной строки с загрузочным файлом не найден!");
            return;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            console.printError("Загрузочный файл '" + fileName + "' не найден!");
            console.println("Будет создана новая пустая коллекция.");
            collection.clear();
            return;
        }

        if (!file.canRead()) {
            console.printError("Нет прав на чтение файла '" + fileName + "'!");
            return;
        }

        if (file.length() == 0) {
            console.println("Файл пуст, создана новая коллекция.");
            collection.clear();
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = bis.readAllBytes();
            String json = new String(bytes).trim();

            if (!json.startsWith("[")) {
                console.printError("Ошибка: файл должен начинаться с '[' (массив объектов)");
                console.println("Создаю новую пустую коллекцию...");
                collection.clear();
                return;
            }

            Vector<Worker> loadedCollection;
            try {
                loadedCollection = mapper.readValue(json, new TypeReference<Vector<Worker>>() {});
            } catch (Exception e) {
                console.printError("Ошибка парсинга JSON: " + e.getMessage());
                console.println("Создаю новую пустую коллекцию...");
                collection.clear();
                return;
            }

            WorkerValidator.CollectionValidationResult validationResult =
                    validator.validateCollection(loadedCollection);

            if (validationResult.hasErrors()) {
                console.printError("При загрузке обнаружены проблемы:");
                for (String error : validationResult.getErrors()) {
                    console.printError("  " + error);
                }
                console.println("Загружено " + validationResult.getValidCount() +
                        " из " + loadedCollection.size() + " элементов");
            }

            collection.clear();
            collection.addAll(validationResult.getValidWorkers());

            console.println("Коллекция Worker успешно загружена! Загружено элементов: " +
                    collection.size());

        } catch (FileNotFoundException e) {
            console.printError("Файл не найден: " + e.getMessage());
            collection.clear();
        } catch (IOException e) {
            console.printError("Ошибка ввода-вывода: " + e.getMessage());
            collection.clear();
        } catch (Exception e) {
            console.printError("Непредвиденная ошибка: " + e.getMessage());
            collection.clear();
        }
    }
}