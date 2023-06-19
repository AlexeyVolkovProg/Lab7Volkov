package org.itmocorp.controller.validators;

import org.itmocorp.model.data.Person;

import java.util.Scanner;

public class InputStringValidator {
    public static String inputValidation(String message) {
        String line = "";
        Scanner scanner = new Scanner(System.in);
        while (line.isEmpty() || line.isBlank()) {
            System.out.println(message);
            line = scanner.nextLine();
            if (line.isEmpty() || line.isBlank()) {
                System.out.println("Введена пустая строка, повторите ввод");
            }
        }
        return line;
    }

    public static String inputPassportValidation(String message, int min, int max){
        String line = "";
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println(message);
            line = scanner.nextLine();
            if (line.isEmpty()) {
                System.out.println("Введена пустая строка, повторите ввод");
                continue;
            }
            if (Person.getArrayId().get(line) == null){
                if (line.length() < min || line.length() > max){
                    System.out.println("Длина строки выходит за пределы. Повторите ввод");
                }else{
                    return line;
                }
            }else{
                System.out.println("Значение поля не оригинально. Повторите ввод");
            }

        } while (true);
    }


}
