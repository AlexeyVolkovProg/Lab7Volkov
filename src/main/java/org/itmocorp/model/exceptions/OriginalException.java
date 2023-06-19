package org.itmocorp.model.exceptions;

public class OriginalException extends RuntimeException{

    public OriginalException(){
        super("Значение поля не является оригинальным");
    }
}
