package org.example.commands;

import org.example.utility.Console;
import org.example.workers.CommandWorker;

public class Help extends Command{
    private final Console console;
    private final CommandWorker commandWorker;

    public Help(Console console, CommandWorker commandWorker) {
        super("help", "Вывести справку по доступным командам");
        this.console = console;
        this.commandWorker = commandWorker;
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
        commandWorker.getCommands().values().forEach(command -> {
            console.printTable(command.getName(), command.getDescription());
        });
        return true;
    }
}
