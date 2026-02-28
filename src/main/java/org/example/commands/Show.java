package org.example.commands;

import org.example.utility.Console;
import org.example.workers.CollectionWorker;

public class Show extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Show(Console console, CollectionWorker collectionWorker) {
        super("show", "Вывести все элементы коллекции");
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

        console.println(collectionWorker);
        return true;
    }
}

