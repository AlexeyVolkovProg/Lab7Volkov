package org.itmocorp.model.parsing;

import org.itmocorp.model.data.Coordinates;
import org.itmocorp.model.data.Person;
import org.itmocorp.model.data.Product;
import org.itmocorp.model.data.UnitOfMeasure;

import java.util.PrimitiveIterator;

public class TransformToProduct {
    private static final int NAME_POSITION = 0;
    private static final int COORDINATE_X_POSITION = 1;
    private static final int COORDINATE_Y_POSITION = 2;
    private static final int PRICE_POSITION = 3;
    private static final int MANUFACTURE_COST_POSITION = 4;
    private static final int UNIT_OF_MEASURE_POSITION = 5;
    private static final int PERSON_NAME_POSITION = 6;
    private static final int PERSON_BIRTHDAY_POSITION = 7;
    private static final int PERSON_PASSPORT_ID_POSITION = 8;

    public static Product transformArguments(String[] args) {
        try {
            for (String arg : args) {
                if (arg.isEmpty()) {
                    throw new Exception();
                }
            }
            String name = args[NAME_POSITION];
            Coordinates coordinates = new Coordinates(Long.parseLong(args[COORDINATE_X_POSITION]), Integer.parseInt(args[COORDINATE_Y_POSITION]));
            Long price = Long.parseLong(args[PRICE_POSITION]);
            Long manufactureCost = Long.parseLong(args[MANUFACTURE_COST_POSITION]);
            UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(args[UNIT_OF_MEASURE_POSITION]);
            Person person = new Person(args[PERSON_NAME_POSITION], args[PERSON_BIRTHDAY_POSITION], args[PERSON_PASSPORT_ID_POSITION]);
            System.out.println("Данные прошли валидацию и был сформирован новый объект коллекции.");
            return new Product(name, coordinates, price, manufactureCost, unitOfMeasure, person);
        } catch (Exception e) {
            System.out.println("Не удалось добавить объект, некоторые данные введены неверно!");
            return null;
        }
    }
}