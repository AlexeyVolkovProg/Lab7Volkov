package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;

public class RemoveFirst extends AbstractCommand{
    public RemoveFirst(){
        name = "removeFirst";
        help = "Удаляет первый элемент коллекции";
    }

    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0){
            CM.printToClient("Команда RemoveFirst начала свое выполнение");
            CommandManager.collection.remove(0);
            CM.printToClient("Команда RemoveFirst начала свое выполнение");
        }else{
            CM.printToClient("Команда не принимает аргументы");
        }
    }
}
