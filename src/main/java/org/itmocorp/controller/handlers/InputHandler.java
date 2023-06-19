package org.itmocorp.controller.handlers;

import org.itmocorp.controller.validators.*;
import org.itmocorp.model.data.Coordinates;
import org.itmocorp.model.data.Person;
import org.itmocorp.model.data.Product;
import org.itmocorp.model.data.UnitOfMeasure;
import org.itmocorp.model.parsing.TransformToProduct;

public class InputHandler {
    private static final int NAME_POSITION = 0;
    private static final int COORDINATE_X_POSITION = 1;
    private static final int COORDINATE_Y_POSITION = 2;
    private static final int PRICE_POSITION = 3;
    private static final int MANUFACTURE_COST_POSITION = 4;
    private static final int UNIT_OF_MEASURE_POSITION = 5;
    private static final int PERSON_NAME_POSITION = 6;
    private static final int PERSON_BIRTHDAY_POSITION = 7;
    private static final int PERSON_PASSPORT_ID_POSITION = 8;

    public static Product ArgumentsReader(){
        String[] arguments = new String[9];
        arguments[NAME_POSITION] = InputStringValidator.inputValidation("Введите название продукта:");
        arguments[COORDINATE_X_POSITION] = InputLongValidator.inputValidation("Введите координату x(целое число типа Long). Максимальное значение поля: 116, Поле не может быть null:", Coordinates.getMIN_X_VALUE(), Coordinates.getMAX_X_VALUE());
        arguments[COORDINATE_Y_POSITION] = InputIntegerValidator.inputValidation("Введите координату y(целое число типа Integer). Максимальное значение поля: 502 ):", Coordinates.getMIN_Y_VALUE(), Coordinates.getMAX_Y_VALUE());
        arguments[PRICE_POSITION] = InputLongValidator.inputValidation("Введите цену товара (целое число типа Long). Поле должно быть больше 0:", Product.getMinPriceValue(), Product.getMaxPriceValue());
        arguments[MANUFACTURE_COST_POSITION] = InputLongValidator.inputValidation("Введите значение цены производства(целое число типа Long). Поле не может быть null.");
        arguments[UNIT_OF_MEASURE_POSITION] = InputEnumValidator.inputValidator("Выберите единицы измерения размера товара (METERS, CENTIMETERS, SQUARE_METERS или PCS):");
        arguments[PERSON_NAME_POSITION] = InputStringValidator.inputValidation("Введите название владельца продукта(поле не может быть null, строка не может быть пустой):");
        arguments[PERSON_BIRTHDAY_POSITION] = InputDateValidator.inputValidator("Введите день рождение владельца товара(значение не может быть null, формат dd/MM/yyyy):", "dd/MM/yyyy");
        arguments[PERSON_PASSPORT_ID_POSITION] = InputStringValidator.inputPassportValidation("Введите passportID владельца строкой(длина строки должна быть не меньше 8, но не больше 28, значение должно быть уникальным и не должно быть null)", Person.getMinLengthPassport(), Person.getMaxLengthPassport());
        return TransformToProduct.transformArguments(arguments);
    }
}