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
import java.util.Vector;

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

    public DumpWorker(String fileName, Console console) {
        this.fileName = fileName;
        this.console = console;
    }

    public void writeCollection(Vector<Worker> collection) {
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
            console.printError("Загрузочный файл не найден! Будет создана новая коллекция.");
            return;
        }

        if (file.length() == 0) {
            console.println("Файл пуст, создана новая коллекция.");
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = bis.readAllBytes();
            String json = new String(bytes).trim();

            System.out.println("   НАЧАЛО JSON ФАЙЛА");
            System.out.println(json);
            System.out.println("   КОНЕЦ ПРЕДПРОСМОТРА");

            if (!json.startsWith("[")) {
                console.printError("Ошибка: файл должен начинаться с '[' (массив объектов)");
                console.println("Создаю новую пустую коллекцию...");
                collection.clear();
                return;
            }

            Vector<Worker> loadedCollection = mapper.readValue(json, new TypeReference<Vector<Worker>>() {});
            collection.clear();
            collection.addAll(loadedCollection);
            console.println("Коллекция Worker успешно загружена! Загружено элементов: " + loadedCollection.size());

        } catch (Exception e) {
            console.printError("Ошибка при чтении файла: " + e.getMessage());
            e.printStackTrace();

            console.println("Создана новая пустая коллекция.");
            collection.clear();
        }
    }
}