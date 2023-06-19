package org.itmocorp.controller.commands;

import org.itmocorp.controller.handlers.InputHandler;
import org.itmocorp.controller.handlers.ScriptHandler;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;

import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GroupCountingById extends AbstractCommand {

    public GroupCountingById(){
        name = "groupCountingById";
        help = "Группирует элементы коллекции по значению поля id, выводит количество элементов в каждой группе";
    }
    private static final HashMap<Integer, Integer> idMap = new HashMap<>();
    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0) {
            CM.printToClient("Была вызвана команда GroupCountingById");
            for (Product product : CommandManager.collection) {
                if (!idMap.containsKey(product.getId())) {
                    idMap.put(product.getId(), 1);
                } else {
                    idMap.put(product.getId(), idMap.get(product.getId()) + 1);
                }
            }
            for (Integer key : idMap.keySet()){
                CM.printToClient("Кол-во объектов с id=" + key + ": " + idMap.get(key));
            }
            CM.printToClient("Команда GroupCountingById завершила свое выполнение");
        }else{
            CM.printToClient("Данная команда не принимает аргументы");
        }
    }
}