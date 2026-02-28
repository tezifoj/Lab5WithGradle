package org.example.commands;

import org.example.utility.Console;
import org.example.workers.CollectionWorker;

public class Clear extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Clear(Console console, CollectionWorker collectionWorker) {
        super("clear", "Очистить коллекцию");
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

        // Проверка, пуста ли уже коллекция
        if (collectionWorker.getCollection().isEmpty()) {
            console.println("Коллекция уже пуста!");
            return true;
        }

        collectionWorker.clear();

        console.println("Коллекция очищена!");
        return true;
    }
}