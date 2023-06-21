package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;

import java.util.stream.Collectors;

public class Clear extends AbstractCommand {

    public Clear() {
        name = "clear";
        help = "Очищает коллекцию";
    }

    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0) {
            synchronized (CommandManager.collection) {
                CM.printToClient("Команда Clear начала выполнение.");
                CommandManager.collection.removeIf(product1 -> product1.getLogin().equals(CM.getLogin()));
                CM.printToClient("Команда Clear закончила выполнение.");
            }
        } else {
            System.out.println("Команда не принимает аргументы");
        }
    }
}
