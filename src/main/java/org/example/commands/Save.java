package org.example.commands;

import org.example.utility.Console;
import org.example.workers.CollectionWorker;

public class Save extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Save(Console console, CollectionWorker collectionWorker) {
        super("save", "Сохранить коллекцию в файл");
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
        collectionWorker.saveCollection();
        return true;
    }
}