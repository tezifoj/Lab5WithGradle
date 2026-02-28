package org.example;

import org.example.commands.*;
import org.example.utility.Runner;
import org.example.utility.StandardConsole;
import org.example.workers.CollectionWorker;
import org.example.workers.CommandWorker;
import org.example.workers.DumpWorker;

public class Main {
    public static void main(String[] args) {
        var console = new StandardConsole();

        if (args.length == 0) {
            console.println(
                    "Введите имя загружаемого файла как аргумент командной строки");
            System.exit(1);
        }

        var dumpWorker = new DumpWorker(args[0], console);
        var collectionWorker = new CollectionWorker(dumpWorker);
        if (!collectionWorker.loadCollection()) {
            System.exit(1);
        }
        ExecuteScript executeScript = new ExecuteScript(console);

        var commandWorker = new CommandWorker() {{
            register("help", new Help(console, this));
            register("info", new Info(console, collectionWorker));
            register("show", new Show(console, collectionWorker));
            register("add", new Add(console, collectionWorker));
            register("update", new Update(console, collectionWorker));
            register("remove_by_id", new RemoveById(console, collectionWorker));
            register("execute_script", executeScript);
            register("clear", new Clear(console, collectionWorker));
            register("save", new Save(console, collectionWorker));
            register("exit", new Exit(console));
            register("add_if_min", new AddIfMin(console, collectionWorker));
            register("shuffle", new Shuffle(console, collectionWorker));
            register("remove_greater", new RemoveGreater(console, collectionWorker));
            register("group_counting_by_organization", new GroupCountingByOrganization(console, collectionWorker));
            register("filter_greater_than_status", new FilterGreaterThanStatus(console, collectionWorker));
            register("print_field_descending_salary", new PrintFieldDescendingSalary(console, collectionWorker));
        }};
        executeScript.setCommandWorker(commandWorker);
        new Runner(console, commandWorker).interactiveMode();
    }
}