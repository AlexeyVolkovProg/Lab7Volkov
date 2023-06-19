package org.itmocorp.model.exceptions;

public class StringFormatException extends RuntimeException{
    public StringFormatException(){
        super("Значение строки не соответствует заданному формату");
    }
}
