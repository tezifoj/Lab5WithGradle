package org.example.commands;

import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

public class RemoveById extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public RemoveById(Console console, CollectionWorker collectionWorker) {
        super("remove_by_id <ID>", "Удалить элемент из коллекции по ID");
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
        // Проверка наличия аргумента
        if (arguments[1].isEmpty()) {
            console.println("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
            return false;
        }

        long id;
        try {
            id = Long.parseLong(arguments[1].trim());
        } catch (NumberFormatException e) {
            console.println("ID не распознан");
            return false;
        }

        Worker worker = collectionWorker.byId(id);
        if (worker == null) {
            console.println("Не существующий ID");
            return false;
        }

        collectionWorker.remove(id);
        collectionWorker.update();
        console.println("Работник успешно удалён!");
        return true;
    }
}