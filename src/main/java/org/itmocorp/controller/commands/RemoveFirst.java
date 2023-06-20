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
            if (CommandManager.collection.size() > 0) {
                if (CommandManager.collection.get(0).getLogin().equals(CM.getLogin()))
                {
                    CommandManager.collection.remove(0);
                    CM.printToClient("Был удален первый элемент коллекции");
                }
                else
                    CM.printToClient("Невозможно удалить первый элемент коллекции. Он не ваш");
            }
            else{
                CM.printToClient("Коллекция итак пуста");
            }
        }else{
            CM.printToClient("Команда не принимает аргументы");
        }
    }
}
