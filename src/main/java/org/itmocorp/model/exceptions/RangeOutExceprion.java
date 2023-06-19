package org.itmocorp.model.exceptions;

public class RangeOutExceprion extends RuntimeException {
    public RangeOutExceprion(){
        super("Значение вышло за допустимый диапазон");
    }
}
