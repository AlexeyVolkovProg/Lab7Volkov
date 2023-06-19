package org.itmocorp.model.exceptions;

public class ValueIsMissing extends RuntimeException{
    public ValueIsMissing(){
        super("Значение данного поля отсутствует");
    }
}
