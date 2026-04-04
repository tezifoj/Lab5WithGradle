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
    private volatile boolean running = true;
    private volatile boolean isShuttingDown = false;

    public Runner(Console console, CommandWorker commandWorker) {
        this.console = console;
        this.commandWorker = commandWorker;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isShuttingDown = true;
            running = false;
        }));
    }

    public void interactiveMode() {
        System.out.println("Программа запущена. Введите 'help' для списка команд.");

        while (running) {
            processCommand();
        }
    }

    private void processCommand() {
        if (!running || isShuttingDown) {
            return;
        }

        try {
            if (running && !isShuttingDown) {
                console.prompt();
            } else {
                return;
            }

            String input = console.readln();

            if (input == null) {
                if (!isShuttingDown) {
                    System.out.println("\nПрограмма прервана пользователем (Ctrl+C)");
                }
                running = false;
                return;
            }

            String trimmedInput = input.trim();

            if (trimmedInput.isEmpty()) {
                return;
            }

            String[] userCommand = parseCommand(trimmedInput);
            commandWorker.addToHistory(userCommand[0]);

            if (userCommand[0].equals("exit")) {
                handleExit();
                return;
            }

            ExecutionResponse commandStatus = launchCommand(userCommand);

            if (!commandStatus.getExitCode() && !commandStatus.getMassage().isEmpty()) {
                console.println(commandStatus.getMassage());
            }

        } catch (NoSuchElementException e) {
            if (!isShuttingDown) {
                System.out.println("\nПрограмма прервана пользователем (Ctrl+C)");
            }
            running = false;
        } catch (Exception e) {
            if (!isShuttingDown) {
                console.printError("Ошибка: " + e.getMessage());
            }
        }
    }

    private String[] parseCommand(String input) {
        String[] result = (input + " ").split(" ", 2);
        result[1] = result[1].trim();
        return result;
    }

    private void handleExit() {
        console.println("Завершение программы...");
        running = false;
    }

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
                    boolean valid = false;
                    while (!valid && running && !isShuttingDown) {
                        try {
                            console.print("> ");
                            String input = console.readln();
                            if (input == null || input.equalsIgnoreCase("exit")) {
                                return false;
                            }
                            if (!input.isEmpty()) {
                                lengthRecursion = Integer.parseInt(input.trim());
                                if (lengthRecursion >= 0 && lengthRecursion <= 500) {
                                    valid = true;
                                } else {
                                    console.println("Глубина должна быть от 0 до 500");
                                }
                            }
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
            boolean scriptRunning = true;

            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            console.selectFileScanner(scriptScanner);

            while (scriptRunning && running && !isShuttingDown) {
                String input = console.readln();
                if (input == null || input.equalsIgnoreCase("exit")) break;
                input = input.trim();
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

                if (!commandStatus.getExitCode() || commandStatus.getMassage().equals("exit") || !console.isCanReadln() || !running) {
                    scriptRunning = false;
                }
            }

            console.selectConsoleScanner();

            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());

        } catch (FileNotFoundException exception) {
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException exception) {
            if (!isShuttingDown) {
                console.printError("Непредвиденная ошибка!");
            }
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return new ExecutionResponse(false, "Неизвестная ошибка");
    }

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
        return new ExecutionResponse(result, result ? "" : "Ошибка при выполнении команды");
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void reset() {
        running = true;
        isShuttingDown = false;
        scriptStack.clear();
        lengthRecursion = -1;
    }
}