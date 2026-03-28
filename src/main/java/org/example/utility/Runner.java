package org.example.utility;

import org.example.workers.CommandWorker;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Runner {
    private Console console;
    private final CommandWorker commandWorker;
    private final List<String> scriptStack = new ArrayList<>();
    private int lengthRecursion = -1;

    public Runner(Console console, CommandWorker commandWorker) {
        this.console = console;
        this.commandWorker = commandWorker;
    }

    /**
     * Интерактивный режим
     */
    public void interactiveMode() {
        try {
            ExecutionResponse commandStatus;
            String[] userCommand = {"", ""};

            while (true) {
                console.prompt();
                String input = console.readln().trim();
                if (input.isEmpty()) continue;

                userCommand = (input + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                commandWorker.addToHistory(userCommand[0]);
                if (userCommand[0].equals("exit")) {
                    console.println("Завершение программы...");
                    break;
                }
                commandStatus = launchCommand(userCommand);
                console.println(commandStatus.getMassage());
            }
        } catch (NoSuchElementException exception) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        }
    }

    /**
     * Проверяет рекурсивность выполнения скриптов.
     * @param argument Название запускаемого скрипта
     * @return можно ли выполнять скрипт.
     */
    private boolean checkRecursion(String argument, Scanner scriptScanner) {
        int recStart = -1;
        int i = 0;
        for (String script : scriptStack) {
            i++;
            if (argument.equals(script)) {
                if (recStart < 0) recStart = i;
                if (lengthRecursion < 0) {
                    console.selectConsoleScanner();
                    console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии (0..500)");
                    while (lengthRecursion < 0 || lengthRecursion > 500) {
                        try {
                            console.print("> ");
                            lengthRecursion = Integer.parseInt(console.readln().trim());
                        } catch (NumberFormatException e) {
                            console.println("длина не распознана");
                        }
                    }
                    console.selectFileScanner(scriptScanner);
                }
                if (i > recStart + lengthRecursion || i > 500)
                    return false;
            }
        }
        return true;
    }

    /**
     * Режим для запуска скрипта.
     * @param argument Аргумент скрипта
     * @return Код завершения.
     */
    private ExecutionResponse scriptMode(String argument) {
        String[] userCommand = {"", ""};
        StringBuilder executionOutput = new StringBuilder();

        File scriptFile = new File(argument);
        if (!scriptFile.exists())
            return new ExecutionResponse(false, "Файл не существует!");
        if (!Files.isReadable(Paths.get(argument)))
            return new ExecutionResponse(false, "Прав для чтения нет!");

        scriptStack.add(argument);
        try (Scanner scriptScanner = new Scanner(scriptFile)) {

            ExecutionResponse commandStatus = new ExecutionResponse("");

            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            console.selectFileScanner(scriptScanner);

            do {
                String input = console.readln().trim();
                if (input.isEmpty() && console.isCanReadln()) continue;

                userCommand = (input + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                executionOutput.append(console.getPrompt()).append(String.join(" ", userCommand)).append("\n");

                boolean needLaunch = true;
                if (userCommand[0].equals("execute_script")) {
                    needLaunch = checkRecursion(userCommand[1], scriptScanner);
                }

                commandStatus = needLaunch ? launchCommand(userCommand) :
                        new ExecutionResponse(false, "Превышена максимальная глубина рекурсии");

                if (userCommand[0].equals("execute_script"))
                    console.selectFileScanner(scriptScanner);

                executionOutput.append(commandStatus.getMassage()).append("\n");

            } while (commandStatus.getExitCode() &&
                    !commandStatus.getMassage().equals("exit") &&
                    console.isCanReadln());

            console.selectConsoleScanner();

            if (!commandStatus.getExitCode() &&
                    !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                executionOutput.append("Проверьте скрипт на корректность введенных данных!\n");
            }

            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());

        } catch (FileNotFoundException exception) {
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return new ExecutionResponse(false, "Неизвестная ошибка");
    }

    /**
     * Запускает команду.
     * @param userCommand Команда для запуска
     * @return Код завершения.
     */
    private ExecutionResponse launchCommand(String[] userCommand) {
        if (userCommand[0].isEmpty()) return new ExecutionResponse("");

        var command = commandWorker.getCommands().get(userCommand[0]);

        if (command == null) {
            return new ExecutionResponse(false,
                    "Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
        }

        if (userCommand[0].equals("execute_script")) {
            boolean result = command.apply(userCommand);
            if (!result) {
                return new ExecutionResponse(false, "Ошибка в аргументах команды execute_script");
            }
            return scriptMode(userCommand[1]);
        }

        boolean result = command.apply(userCommand);
        return new ExecutionResponse(result, result ? "Команда выполнена успешно" : "Ошибка при выполнении команды");
    }
}