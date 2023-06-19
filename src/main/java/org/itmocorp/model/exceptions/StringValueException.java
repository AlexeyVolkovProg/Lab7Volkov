package org.itmocorp.model.exceptions;

public class StringValueException extends RuntimeException{
    public StringValueException(){
        super("Длина строки выходит за заданные пределы");
    }
}
