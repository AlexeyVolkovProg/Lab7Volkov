package org.itmocorp.controller.validators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class InputDateValidator {

    public static String inputValidator(String message, String dateFormat){
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println(message);
            String dateToValidate = scanner.nextLine();
            if (isThisDateValid(dateToValidate, dateFormat)){
                return dateToValidate;
            }else{
                System.out.println("Введенные вами данные не соответствуют нужному формату, повторите попытку");
            }
        }
    }

    public static boolean isThisDateValid(String dateToValidate, String dateFormat){
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateToValidate);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
