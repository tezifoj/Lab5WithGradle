package org.example.commands;

import org.example.models.Status;
import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.util.Vector;

public class FilterGreaterThanStatus extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public FilterGreaterThanStatus(Console console, CollectionWorker collectionWorker) {
        super("filter_greater_than_status", "Вывести элементы, значение поля status которого больше заданного");
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
            if (arguments[1].isEmpty()) { // Проверка, что аргумент передан
                console.println("Неправильное количество аргументов!");
                console.println("Использование: '" + getName() + "'");
                return false;
            }
            String statusArg = arguments[1].trim();
            Status filterStatus;

            try {
                filterStatus = Status.valueOf(statusArg.toUpperCase());
            } catch (IllegalArgumentException e) {
                console.println("Статус '" + statusArg + "' не существует!");
                console.println("Доступные статусы: " + Status.names());
                return false;
            }

            Vector<Worker> collection = collectionWorker.getCollection();
            if (collection.isEmpty()) {
                console.println("Коллекция пуста!");
                return false;
            }
            Vector<Worker> filteredWorkers = new Vector<>();
            for (Worker worker : collection) {
                Status workerStatus = worker.getStatus();

                if (workerStatus != null && workerStatus.compareTo(filterStatus) > 0) {
                    filteredWorkers.add(worker);
                }
            }

            if (filteredWorkers.isEmpty()) {
                console.println("Работники со статусом больше '" + filterStatus + "' не найдены.");
            } else {
                console.println("Найдено работников со статусом больше '" + filterStatus + "': " + filteredWorkers.size());

                for (int i = 0; i < filteredWorkers.size(); i++) {
                    Worker worker = filteredWorkers.get(i);
                    console.println("Работник #" + (i + 1) + ":");
                    console.println("ID: " + worker.getId());
                    console.println("Имя: " + worker.getName());
                    console.println("Статус: " + worker.getStatus());
                    console.println("Зарплата: " + (worker.getSalary() != null ? worker.getSalary() : "не указана"));
                    console.println("Координаты: (" + worker.getCoordinates().getX() + ", " + worker.getCoordinates().getY() + ")");

                    if (worker.getOrganization() != null) {
                        console.println("Организация: " + worker.getOrganization().getType() +
                                " (сотрудников: " + worker.getOrganization().getEmployeesCount() + ")");
                    } else {
                        console.println("Организация: не указана");
                    }

                    console.println("Дата создания: " + worker.getCreationDate());
                    console.println("Дата начала: " + worker.getStartDate());
                    console.println("Дата окончания: " + (worker.getEndDate() != null ? worker.getEndDate() : "не указана"));
                    console.println("");
                }
            }
            return true;

        } catch (Exception e) {
            console.printError("Ошибка при выполнении команды filter_greater_than_status: " + e.getMessage());
            return false;
        }
    }
}
