package org.example.commands;

import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.util.Collections;
import java.util.Vector;

public class Shuffle extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Shuffle(Console console, CollectionWorker collectionWorker) {
        super("shuffle", "Перемешать элементы коллекции в случайном порядке");
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
        try {
            if (!arguments[1].isEmpty()) {
                console.println("Неправильное количество аргументов!");
                console.println("Использование: '" + getName() + "'");
                return false;
            }
            Vector<Worker> collection = collectionWorker.getCollection();
            if (collection.isEmpty()) {
                console.println("Коллекция пуста! Нечего перемешивать.");
                return false;
            }

            int oldSize = collection.size();

            Collections.shuffle(collection);

            if (collection.size() == oldSize) {
                console.println("Коллекция успешно перемешана!");
                console.println("Текущий размер коллекции: " + collection.size());
                return true;
            } else {
                console.println("Ошибка: размер коллекции изменился после перемешивания!");
                return false;
            }

        } catch (Exception e) {
            console.printError("Ошибка при выполнении команды shuffle: " + e.getMessage());
            return false;
        }
    }
}