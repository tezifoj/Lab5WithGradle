package org.example.workers;

import org.example.commands.Command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class CommandWorker {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Vector<String> commandHistory = new Vector<>();

    /**
     * Добавляет команду
     *
     * @param commandName Название команды
     * @param command     Команда
     */
    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    /**
     * @return Словарь команд
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * @return Историю команд
     */
    public Vector<String> getCommandHistory() {
        return commandHistory;
    }

    /**
     * Добавляет команду в историю
     * @param command Команда
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
    }
}
