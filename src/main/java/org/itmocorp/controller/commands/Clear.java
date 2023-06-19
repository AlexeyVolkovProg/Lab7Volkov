package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;

public class Clear extends AbstractCommand{

    public Clear(){
        name = "clear";
        help = "Очищает коллекцию";
    }
    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0) {
            CM.printToClient("Команда Clear начала выполнение.");
            CommandManager.collection.clear();
            CM.printToClient("Команда Clear закончила выполнение.");
        }else{
            System.out.println("Команда не принимает аргументы");
        }
    }
}
