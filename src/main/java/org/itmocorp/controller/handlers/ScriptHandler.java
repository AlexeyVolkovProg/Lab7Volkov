package org.itmocorp.controller.handlers;

import org.itmocorp.Client;
import org.itmocorp.controller.commands.AbstractCommand;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;
import org.itmocorp.model.parsing.TransformToProduct;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ScriptHandler {
    private static String currentFilePath;
    private static final HashMap<String, Scanner> scannersMap = new HashMap<>();

    public static void startFile(String filename, Client client) {
        try {
            File file0 = new File(filename);
            String filepath = file0.getAbsolutePath();
            filename = file0.getAbsolutePath();
            if (scannersMap.containsKey(filepath)) {
                System.out.println("Скрипт, расположенный по пути " + filepath + " не может быть исполнен, так как в этом случае будет рекурсия.");
            } else {
                currentFilePath = filepath;
                System.out.println(currentFilePath);
                File file1 = new File(filepath);
                System.out.println("Выполнение файла " + file1.getName() + "было начато.");
                Scanner scanner = new Scanner(file1);
                scannersMap.put(filepath, scanner);
                while (scannersMap.get(filename).hasNextLine()) {

                    lineHandler(scannersMap.get(filename).nextLine(), client);
                }
                client.commandManager.setScriptStatus(false);
                System.out.println("Выполнение файла " + file1.getName() + "было окончено.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти файл по указанному вами пути.");
        }
        catch (NullPointerException e){
            System.out.println("Не удалось найти файл");
        }
    }


    private static void lineHandler(String line, Client client) {
        try {
            String[] input = line.trim().split("\\s+", 2);
            String commandName = input[0];
            String argument = input.length > 1 ? input[1] : "";
            AbstractCommand command = CommandManager.commands.get(commandName);
            if (command == null) {
                System.out.println("Команда \"" + commandName + "\" не найдена. Введите \"help\" для списка команд.");
            } else {
                try {
                    client.commandManager.setScriptStatus(true);
                    String[] arg;
                    if (!argument.equals("")) {
                        arg = new String[1]; // извиняюсь, за это
                        arg[0] = argument;
                    }else{
                        arg = new String[0];
                    }
                    command.setArgs(arg);


                    client.addCommand(command);
                    if (command.isNeedObjectToExecute())
                        client.addProduct(getProductFromFile()); //client.CM.getProduct());
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка выполнения команды \"" + commandName + "\". Проверьте, правильно вы ввели аргумент команды.");
                }
            }
        }catch (NoSuchElementException e){
            System.out.println("Ошибка выполнения команды из-за введенного необрабатываемого символа");
        }
    }


    public static Product getProductFromFile() {
        String[] args = new String[9];
        for (int i = 0; i < 9; i++) {
            args[i] = scannersMap.get(currentFilePath).nextLine();
        }
        return TransformToProduct.transformArguments(args);
    }
}

//
//    private static Scanner scanner;
//    private static HashSet<String> scriptsNames = new HashSet<>();
//
//    public static void startFile(String filename) {
//        try {
//            String filepath = new File(filename).getAbsolutePath();
//            if (scriptsNames.contains(filepath)) {
//                System.out.println("Скрипт, расположенный по пути " + filepath + " не может быть исполнен, так как в этом случае будет рекурсия.");
//            } else {
//                File file1 = new File(filepath);
//                scriptsNames.add(filepath);
//                System.out.println("Выполнение файла " + file1.getName() + "было начато.");
//                scanner = new Scanner(file1);
//                while (scanner.hasNextLine()) {
//                    lineHandler(scanner.nextLine());
//                }
//                CommandManager.setScriptStatus(false);
//                System.out.println("Выполнение файла " + file1.getName() + "было окончено.");
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("Не удалось найти файл по указанному вами пути.");
//        }
//    }
//
//
//    private static void lineHandler(String line) {
//
//        String[] input = line.trim().split("\\s+", 2);
//        String commandName = input[0];
//        String argument = input.length > 1 ? input[1] : "";
//        AbstractCommand command = CommandManager.commands.get(commandName);
//        if (command == null) {
//            System.out.println("Команда \"" + commandName + "\" не найдена. Введите \"help\" для списка команд.");
//        } else {
//            try {
//                CommandManager.setScriptStatus(true);
//                command.execute(argument);
//            } catch (IllegalArgumentException e) {
//                System.out.println("Ошибка выполнения команды \"" + commandName + "\". Проверьте, правильно вы ввели аргумент команды.");
//            }
//        }
//    }
//
//
//    public static Product getProductFromFile() {
//        String[] args = new String[9];
//        for (int i = 0; i < 9; i++) {
//            args[i] = scanner.nextLine();
//        }
//        return TransformToProduct.transformArguments(args);
//    }