package org.example.workers;

import org.example.models.Worker;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CollectionWorker {
    private Long currentId = 1L;
    private Map<Long, Worker> workers = new HashMap<>();
    private Vector<Worker> collection = new Vector<>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private final DumpWorker dumpWorker;

    public CollectionWorker(DumpWorker dumpWorker) {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.dumpWorker = dumpWorker;
    }

    /**
     * @return Последнее время инициализации
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * @return Последнее время сохранения
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * @return коллекция
     */
    public Vector<Worker> getCollection() {
        return collection;
    }

    /**
     * Получить Worker по id
     */
    public Worker byId(Long id) {
        return workers.get(id);
    }

    /**
     * Проверка на содержание коллекций Worker
     */
    public boolean isContain(Worker e) {
        return e == null || byId(e.getId()) != null;
    }

    /**
     * Получить свободный ID (минимальный незанятый положительный ID)
     */
    public Long getFreeId() {
        for (long id = 1; id <= currentId + 1; id++) {
            if (byId(id) == null) {
                currentId = id;
                return id;
            }
        }
        currentId++;
        return currentId;
    }
    /**
     * Добавляет Worker
     */
    public boolean add(Worker a) {
        if (isContain(a)) return false;
        workers.put(a.getId(), a);
        collection.add(a);
        update();
        return true;
    }

    /**
     * Удаляет Worker по ID
     */
    public boolean remove(Long id) {
        var a = byId(id);
        if (a == null) return false;
        workers.remove(a.getId());
        collection.remove(a);
        update();
        return true;
    }

    /**
     * Зафиксировать изменения коллекции
     */
    public void update() {
        Collections.sort(collection);
    }

    /**
     * Сохраняет коллекцию в файл
     */
    public void saveCollection() {
        dumpWorker.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

    /**
     * Загружает коллекцию из файла.
     * @return true в случае успеха.
     */
    public boolean loadCollection() {
        workers.clear();
        dumpWorker.readCollection(collection);
        lastInitTime = LocalDateTime.now();
        for (var e : collection)
            if (byId(e.getId()) != null) {
                collection.clear();
                return false;
            } else {
                if (e.getId()>currentId) currentId = e.getId();
                workers.put(e.getId(), e);
            }
        update();
        return true;
    }

    public void clear() {
        collection.clear();
        workers.clear();
        update();
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";

        StringBuilder info = new StringBuilder();
        for (var worker : collection) {
            info.append(worker + "\n\n");
        }
        return info.toString().trim();
    }
}