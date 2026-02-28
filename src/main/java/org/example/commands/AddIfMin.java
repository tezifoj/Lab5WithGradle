package org.example.commands;

import org.example.models.Ask;
import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.util.Vector;

public class AddIfMin extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public AddIfMin(Console console, CollectionWorker collectionWorker) {
        super("add_if_min {element}", "Добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
        this.console = console;
        this.collectionWorker = collectionWorker;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            console.println("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
            return false;
        }

        try {
            console.println("Создание нового работника для сравнения:");
            Worker newWorker = Ask.askWorker(console, Math.toIntExact(collectionWorker.getFreeId()));

            if (newWorker == null) {
                console.println("Поля работника не валидны! Работник не создан!");
                return false;
            }
            Vector<Worker> collection = collectionWorker.getCollection();
            if (collection.isEmpty()) {
                collectionWorker.add(newWorker);
                console.println("Коллекция была пуста. Работник успешно добавлен!");
                return true;
            }
            Worker minWorker = collection.stream()
                    .min((w1, w2) -> {
                        if (w1.getSalary() == null && w2.getSalary() == null) return 0;
                        if (w1.getSalary() == null) return -1;
                        if (w2.getSalary() == null) return 1;
                        return Double.compare(w1.getSalary(), w2.getSalary());
                    })
                    .orElse(null);
            if (minWorker != null) {
                boolean isLess;
                if (newWorker.getSalary() == null && minWorker.getSalary() == null) {
                    isLess = false;
                } else if (newWorker.getSalary() == null) {
                    isLess = true;
                } else if (minWorker.getSalary() == null) {
                    isLess = false;
                } else {
                    isLess = newWorker.getSalary() < minWorker.getSalary();
                }
                if (isLess) {
                    collectionWorker.add(newWorker);
                    console.println("Работник успешно добавлен, так как его salary (" +
                            (newWorker.getSalary() != null ? newWorker.getSalary() : "null") +
                            ") меньше минимального (" +
                            (minWorker.getSalary() != null ? minWorker.getSalary() : "null") + ")");
                    return true;
                } else {
                    console.println("Работник не добавлен, так как его salary (" +
                            (newWorker.getSalary() != null ? newWorker.getSalary() : "null") +
                            ") не меньше минимального (" +
                            (minWorker.getSalary() != null ? minWorker.getSalary() : "null") + ")");
                    return false;
                }
            }
            return false;

        } catch (Ask.AskBreak e) {
            console.println("Отмена создания работника...");
            return false;
        } catch (Exception e) {
            console.printError("Ошибка при выполнении команды: " + e.getMessage());
            return false;
        }
    }
}