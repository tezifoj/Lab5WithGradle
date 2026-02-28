package org.example.commands;

import org.example.models.Ask;
import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import static java.lang.Math.toIntExact;

public class Add extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Add(Console console, CollectionWorker collectionWorker) {
        super("add {element}", "Добавить новый элемент в коллекцию");
        this.console = console;
        this.collectionWorker = collectionWorker;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды
     */
    @Override
    public boolean apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            console.println("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
            return false;
        }

        try {
            console.println("Добавление информации о новом работнике:");
            Worker w = Ask.askWorker(console, toIntExact(collectionWorker.getFreeId()));

            if (w != null) {
                collectionWorker.add(w);
                console.println("Работник успешно добавлен!");
                return true;
            } else {
                console.println("Поля работника не валидны! Работник не создан!");
                return false;
            }
        } catch (Ask.AskBreak e) {
            console.println("Отмена...");
            return false;
        }
    }
}