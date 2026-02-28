package org.example.commands;

import org.example.models.Organization;
import org.example.models.Worker;
import org.example.utility.Console;
import org.example.workers.CollectionWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GroupCountingByOrganization extends Command {
    private final Console console;
    private final CollectionWorker collectionWorker;

    public GroupCountingByOrganization(Console console, CollectionWorker collectionWorker) {
        super("group_counting_by_organization", "Сгруппировать элементы коллекции по значению поля organization, вывести количество элементов в каждой группе");
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
                console.println("Коллекция пуста! Нечего группировать.");
                return false;
            }
            Map<Organization, Integer> organizationGroups = new HashMap<>();

            for (Worker worker : collection) {
                Organization org = worker.getOrganization();

                organizationGroups.put(org, organizationGroups.getOrDefault(org, 0) + 1);
            }

            console.println("Результаты группировки по организациям:");

            int groupNumber = 1;
            for (Map.Entry<Organization, Integer> entry : organizationGroups.entrySet()) {
                Organization org = entry.getKey();
                Integer count = entry.getValue();

                console.println("Группа #" + groupNumber++ + ":");

                if (org == null) {
                    console.println("Организация: null (работники без организации)");
                } else {
                    console.println("Организация:");
                    console.println("Количество сотрудников: " + org.getEmployeesCount());
                    console.println("Тип организации: " + org.getType());
                    console.println("Адрес: " + org.getOfficialAddress());
                }
                console.println("");
                console.println("Количество работников в группе: " + count);
                console.println("");
            }

            return true;

        } catch (Exception e) {
            console.printError("Ошибка при выполнении команды group_counting_by_organization: " + e.getMessage());
            return false;
        }
    }
}
