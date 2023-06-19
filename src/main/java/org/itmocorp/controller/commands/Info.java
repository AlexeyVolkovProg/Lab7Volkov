package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;

import java.rmi.activation.ActivationGroup_Stub;

public class Info extends AbstractCommand {
    public Info(){
        name = "info";
        help = "Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)";
    }

    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0){
            //System.out.println("Команда info начала свое выполнение");
            CM.printToClient("Тип коллекции: " + CommandManager.collection.getClass().getName());
            CM.printToClient("Время создания коллекции: " + CommandManager.collectionTime);
            CM.printToClient("Количество элементов в коллеции: " + CommandManager.collection.size());
            //System.out.println("Команда info закончила свое выполнение");
        }else{
            CM.printToClient("Данная команда не принимает аргументы");
        }
    }
}
