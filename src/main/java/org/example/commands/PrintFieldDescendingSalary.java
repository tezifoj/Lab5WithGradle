package org.example.commands;

import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class PrintFieldDescendingSalary extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public PrintFieldDescendingSalary(Console console, CollectionWorker collectionWorker) {
        super("print_field_descending_salary", "Вывести значение поля salary всех элементов в порядке убывания");
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
                console.println("Коллекция пуста!");
                return false;
            }

            List<Double> salaries = new ArrayList<>();
            int nullCount = 0;

            for (Worker worker : collection) {
                Double salary = worker.getSalary();
                if (salary != null) {
                    salaries.add(salary);
                } else {
                    nullCount++;
                }
            }

            salaries.sort(Comparator.reverseOrder());

            console.println("Значения поля salary всех элементов в порядке убывания:");

            if (salaries.isEmpty()) {
                console.println("Нет элементов с указанным значением salary (все salary = null)");
            } else {
                for (int i = 0; i < salaries.size(); i++) {
                    console.println((i + 1) + ". " + salaries.get(i));
                }
            }
            return true;

        } catch (Exception e) {
            console.printError("Ошибка при выполнении команды print_field_descending_salary: " + e.getMessage());
            return false;
        }
    }
}
