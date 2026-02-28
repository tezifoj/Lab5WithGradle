package org.example.commands;

import org.example.models.Ask;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

public class Update extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Update(Console console, CollectionWorker collectionWorker) {
        super("update <ID> {element}", "Обновить значение элемента коллекции по ID");
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

        var old = collectionWorker.byId(id);
        if (old == null || !collectionWorker.getCollection().contains(old)) {
            console.println("Не существующий ID");
            return false;
        }

        try {
            console.println("Изменение информации о работнике:");
            var d = Ask.askWorker(console, Math.toIntExact(old.getId()));

            if (d != null) {
                collectionWorker.remove(old.getId());
                collectionWorker.add(d);
                collectionWorker.update();
                console.println("Работник обновлен!");
                return true;
            } else {
                console.println("Поля работника не валидны! Работник не создан!");
                return false;
            }
        } catch (Ask.AskBreak e) {
            console.println("Отмена обновления работника...");
            return false;
        }
    }
}