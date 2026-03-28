package org.example.workers;

import org.example.models.Worker;

import java.time.LocalDateTime;
import java.util.*;

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

    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    public Vector<Worker> getCollection() {
        return collection;
    }

    public Worker byId(Long id) {
        return workers.get(id);
    }

    public boolean isContain(Worker e) {
        return e == null || byId(e.getId()) != null;
    }

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

    public boolean add(Worker a) {
        if (isContain(a)) return false;
        workers.put(a.getId(), a);
        collection.add(a);
        update();
        return true;
    }

    public boolean remove(Long id) {
        var a = byId(id);
        if (a == null) return false;
        workers.remove(a.getId());
        collection.remove(a);
        update();
        return true;
    }

    public void update() {
        Collections.sort(collection);
    }

    public void saveCollection() {
        dumpWorker.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

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