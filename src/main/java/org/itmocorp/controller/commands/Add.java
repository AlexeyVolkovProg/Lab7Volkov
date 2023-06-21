package org.itmocorp.controller.commands;


import org.itmocorp.controller.handlers.ScriptHandler;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.controller.handlers.InputHandler;
import org.itmocorp.model.data.Product;

public class Add extends AbstractCommand {

    public Add() {
        name = "add";
        help = "Команда позволяет добавить новый элемент в коллекцию";
        needObjectToExecute = true;
    }

    @Override
    public void execute(CommandManager CM) {
        CM.printToClient("Была вызвана команда ADD");
        if (args.length == 0) {
            synchronized (CommandManager.collection) {
                if (!CM.isScriptStatus()) {
                    if (product != null) {
                        CommandManager.collection.add(product);
                        CM.printToClient("Новый объект был успешно добавлен в вашу коллекцию");
                    } else {
                        CM.printToClient("Новый объект не был добавлен в коллекцию, так не удалось его сформировать");
                    }
                } else {
                    if (product != null) {
                        CommandManager.collection.add(product);
                    } else {
                        CM.printToClient("Новый объект не был добавлен в коллекцию, так не удалось его сформировать");
                    }
                }
            }
        } else {
            CM.printToClient("На данном этапе команда не принимает аргументы.");
        }
    }


}
