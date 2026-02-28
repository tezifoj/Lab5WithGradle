package org.example.commands;

import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.time.LocalDateTime;

public class Info extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public Info(Console console, CollectionWorker collectionWorker) {
        super("info", "Вывести информацию о коллекции");
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

        LocalDateTime lastInitTime = collectionWorker.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? "в данной сессии инициализации еще не происходило" :
                lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();

        LocalDateTime lastSaveTime = collectionWorker.getLastSaveTime();
        String lastSaveTimeString = (lastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();

        console.println("Сведения о коллекции:");
        console.println(" Тип: " + collectionWorker.getCollection().getClass());
        console.println(" Количество элементов: " + collectionWorker.getCollection().size());
        console.println(" Дата последнего сохранения: " + lastSaveTimeString);
        console.println(" Дата последней инициализации: " + lastInitTimeString);
        return true;
    }
}
