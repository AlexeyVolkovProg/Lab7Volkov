package org.itmocorp.controller.validators;

import java.util.Scanner;

public class InputLongValidator {
    public static String inputValidation(String message, long min, long max) {
        String line = "";
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                System.out.println(message);
                line = scanner.nextLine();
                if ((Long.parseLong(line) > max || Long.parseLong(line) <= min)) {
                    System.out.print("Значение вне диапазона.");
                }
                else {
                    return line;
                }

            } catch (NumberFormatException e){
                System.out.print("Введены неверные данные.");
            }
        } while (true);
    }

    public static String inputValidation(String message){
        return inputValidation(message, Long.MIN_VALUE, Long.MAX_VALUE);
    }
}
