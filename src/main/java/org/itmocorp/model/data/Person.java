package org.itmocorp.model.data;

import org.itmocorp.model.exceptions.OriginalException;
import org.itmocorp.model.exceptions.StringFormatException;
import org.itmocorp.model.exceptions.StringValueException;
import org.itmocorp.model.exceptions.ValueIsMissing;
import org.xml.sax.SAXNotRecognizedException;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String birthday; //Поле не может быть null
    private String passportID; //Длина строки должна быть не меньше 8, Длина строки не должна быть больше 28, Значение этого поля должно быть уникальным, Поле не может быть null
    private static final int MIN_LENGTH_PASSPORT = 8;
    private static final int MAX_LENGTH_PASSPORT = 28;
    private static Map<String, Boolean> arrayId = new HashMap<>();

    private boolean OriginalFlag = true;

    public Person(String name, String birthday, String passportID) {
        setName(name);
        setBirthday(birthday);
        setPassportID(passportID);
    }

    public void setName(String name) {
        if (name == null){
            throw new NullPointerException();
        }else if(name.isEmpty()){
            throw  new ValueIsMissing();
        }else{
            this.name = name;
        }
    }

    public void setBirthday(String birthday) {
        if (birthday == null){
            throw new NullPointerException();
        }else{
            if (isThisDateValid(birthday, "dd/MM/yyyy")) {
                this.birthday = birthday;
            }else{
                throw new StringFormatException();
            }
        }
    }
    public void setPassportID(String passportId) {
        OriginalFlag = arrayId.get(passportId) == null;
        if (OriginalFlag){
            if (passportId.length() < 8 || passportId.length() > 28){
                throw new StringValueException();
            }else{
                this.passportID = passportId;
                arrayId.put(passportId, true);
            }
        }else{
            throw new OriginalException();
        }
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPassportID() {
        return passportID;
    }

    public static Map<String, Boolean> getArrayId(){
        return arrayId;
    }
    public static Integer getMinLengthPassport(){
        return MIN_LENGTH_PASSPORT;
    }
    public static Integer getMaxLengthPassport(){
        return MAX_LENGTH_PASSPORT;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", passportID='" + passportID + '\'' +
                ", OriginalFlag=" + OriginalFlag +
                '}';
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




