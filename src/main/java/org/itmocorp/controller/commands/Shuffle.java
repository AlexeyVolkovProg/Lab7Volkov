package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Shuffle extends AbstractCommand{

    public Shuffle(){
        name = "shuffle";
        help = "Перемешивает элементы коллекции в случайном порядке.";
    }

    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0){
            synchronized (CommandManager.collection) {
                CM.printToClient("Команда Shuffle начала выполнение");
                Collections.shuffle(CommandManager.collection);
                CM.printToClient("Команда Shuffle закончила выполнение");
            }
        }else{
            CM.printToClient("Данная команда не принимает аргументы");
        }
    }
}
