package org.example.commands;

import org.example.models.Ask;
import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.util.Vector;

public class RemoveGreater extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public RemoveGreater(Console console, CollectionWorker collectionWorker) {
        super("remove_greater {element}", "Удалить из коллекции все элементы, превышающие заданный");
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
                console.println("Коллекция пуста! Нечего удалять.");
                return false;
            }

            console.println("Создание эталонного работника для сравнения:");
            console.println("(Все работники с salary больше чем у этого будут удалены)");

            Worker referenceWorker = Ask.askWorker(console, Math.toIntExact(collectionWorker.getFreeId()));

            if (referenceWorker == null) {
                console.println("Поля работника не валидны! Работник не создан!");
                return false;
            }

            Double referenceSalary = referenceWorker.getSalary();

            Vector<Worker> toRemove = new Vector<>();

            for (Worker worker : collection) {
                Double workerSalary = worker.getSalary();
                boolean isGreater = false;

                if (referenceSalary == null && workerSalary == null) {
                    isGreater = false;
                } else if (referenceSalary == null) {
                    isGreater = true;
                } else if (workerSalary == null) {
                    isGreater = false;
                } else {
                    isGreater = workerSalary > referenceSalary;
                }

                if (isGreater) {
                    toRemove.add(worker);
                }
            }

            int removedCount = 0;
            for (Worker worker : toRemove) {
                if (collectionWorker.remove(worker.getId())) {
                    removedCount++;
                }
            }

            collectionWorker.update();

            if (removedCount == 0) {
                console.println("Нет работников с salary больше чем у эталонного ("
                        + (referenceSalary != null ? referenceSalary : "null") + ")");
            } else {
                console.println("Удалено работников: " + removedCount);
                console.println("Эталонный работник не был добавлен в коллекцию");
            }

            return true;

        } catch (Ask.AskBreak e) {
            console.println("Отмена создания работника...");
            return false;
        } catch (Exception e) {
            console.printError("Ошибка при выполнении команды remove_greater: " + e.getMessage());
            return false;
        }
    }
}