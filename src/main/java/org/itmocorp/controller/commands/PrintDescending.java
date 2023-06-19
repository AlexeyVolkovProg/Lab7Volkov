package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;
import org.itmocorp.model.managers.CollectionManager;

import java.util.*;
import java.util.stream.Collectors;

public class PrintDescending extends AbstractCommand {
    public PrintDescending(){
        name = "printDescending";
        help = "Выводит элементы коллекции в порядке убывания.";
    }
//    @Override
//    public void execute() {
//        if (args.length == 0) {
//            CommandManager.collection
//                    .stream()
//                    .sorted((p1, p2) -> (int) (p2.getPrice() - p1.getPrice()))
//                    .forEach(System.out::println);
//        }else{
//            CommandManager.printToClient("Данная команда не принимает аргументы");
//        }
//    }
    @Override
    public void execute(CommandManager CM) {
        if (args.length == 0) {
            CommandManager.collection
                    .stream()
                    .sorted((p1, p2) -> (int) (p2.getPrice() - p1.getPrice()))
                    .forEach(product -> CM.printToClient(product.toString()));
        } else {
            CM.printToClient("Данная команда не принимает аргументы");
        }
    }
}