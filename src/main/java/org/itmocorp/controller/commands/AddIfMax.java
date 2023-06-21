package org.itmocorp.controller.commands;

import org.itmocorp.controller.handlers.InputHandler;
import org.itmocorp.controller.handlers.ScriptHandler;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;

import java.util.Collections;

public class AddIfMax extends AbstractCommand{

    public AddIfMax(){
        name = "addIfMax";
        help = "Добавление нового элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции(сравнение производится по полю price)";
        needObjectToExecute = true;
    }

    @Override
    public void execute(CommandManager CM) {
        CM.printToClient("Была вызвана команда AddIfMax");
        if (args.length == 0) {
            synchronized (CommandManager.collection) {
                if (!CM.isScriptStatus()) {
                    Product product = InputHandler.ArgumentsReader();
                    if (product != null && product.compareTo(Collections.max(CommandManager.collection)) > 0) {
                        CommandManager.collection.add(product);
                        CM.printToClient("Новый объект был успешно добавлен в вашу коллекцию");
                    } else {
                        CM.printToClient("Новый объект не был добавлен в коллекцию, так не удалось его сформировать или его значение не превышало значение максимального элемента коллекции");
                    }
                } else {
                    Product product = ScriptHandler.getProductFromFile();
                    if (product != null && product.compareTo(Collections.max(CommandManager.collection)) > 0) {
                        CommandManager.collection.add(product);
                        CM.printToClient("Новый объект был успешно добавлен в вашу коллекцию");
                    } else {
                        CM.printToClient("Новый объект не был добавлен в коллекцию, так не удалось его сформировать или его значение не превышало значение максимального элемента коллекции");
                    }
                }
            }
        }else{
            CM.printToClient("На данном этапе команда не принимает аргументы.");
        }
        CM.printToClient("Команда AddIfMax закончила свое выполнение.");
    }
}
