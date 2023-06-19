package org.itmocorp.controller.validators;

import org.itmocorp.model.data.UnitOfMeasure;

import java.util.Scanner;

public class InputEnumValidator {

    public static String inputValidator(String message) {
        while (true) {
            try {
                System.out.println(message);
                Scanner scanner = new Scanner(System.in);
                String measure = scanner.nextLine();
                UnitOfMeasure.valueOf(measure);
                return measure;
            } catch (IllegalArgumentException e) {
                System.out.println("Неверное значение размера. Попробуйте снова:");
            }
        }
    }
}
