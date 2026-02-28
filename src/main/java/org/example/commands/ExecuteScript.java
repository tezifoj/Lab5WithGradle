package org.example.commands;

import org.example.utility.Console;
import org.example.workers.CommandWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ExecuteScript extends Command {
    private final Console console;
    private CommandWorker commandWorker;

    public ExecuteScript(Console console) {
        super("execute_script <file_name>", "Исполнить скрипт из указанного файла");
        this.console = console;
    }

    /**
     * Устанавливает CommandWorker для выполнения скриптов
     *
     * @param commandWorker менеджер команд
     */
    public void setCommandWorker(CommandWorker commandWorker) {
        this.commandWorker = commandWorker;
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

        if (commandWorker == null) {
            console.printError("Внутренняя ошибка: CommandWorker не инициализирован!");
            return false;
        }

        String fileName = arguments[1].trim();
        File scriptFile = new File(fileName);

        if (!scriptFile.exists()) {
            console.printError("Файл '" + fileName + "' не существует!");
            return false;
        }

        if (!scriptFile.canRead()) {
            console.printError("Нет прав на чтение файла '" + fileName + "'!");
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(scriptFile);
             Scanner fileScanner = new Scanner(fileInputStream)) {

            console.println("Выполнение скрипта '" + fileName + "'...");

            int lineNumber = 0;
            int successCount = 0;
            int errorCount = 0;

            console.selectFileScanner(fileScanner);

            while (fileScanner.hasNextLine()) {
                lineNumber++;
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                console.println("\n[Строка " + lineNumber + "] Выполнение: " + line);

                String[] userCommand = (line + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                if (userCommand[0].equals("execute_script")) {
                    console.printError("Рекурсивный вызов execute_script запрещен!");
                    errorCount++;
                    continue;
                }

                Command command = commandWorker.getCommands().get(userCommand[0]);

                if (command == null) {
                    console.printError("Команда '" + userCommand[0] + "' не найдена!");
                    errorCount++;
                    continue;
                }

                boolean result = command.apply(userCommand);

                if (result) {
                    successCount++;
                } else {
                    errorCount++;
                }
            }

            // Возвращаем консоль в обычный режим
            console.selectConsoleScanner();

            console.println("\nВыполнение скрипта завершено.");
            console.println("Обработано строк: " + lineNumber);
            console.println("Успешно: " + successCount);
            console.println("Ошибок: " + errorCount);

            return errorCount == 0;

        } catch (IOException e) {
            console.printError("Ошибка при чтении файла: " + e.getMessage());
            return false;
        }
    }
}