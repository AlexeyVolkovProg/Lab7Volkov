package org.itmocorp;

import java.io.*;

/**
 * Класс, который отвечает на сериализацию и десериализацию объектов
 *
 * @author Alexey Volkov, P3113
 */

public class Serialization {
    public static final int LOGIN_POSITION = 0;
    public static final int PASSWORD_POSITION = 1;
    public static final int OBJECT_POSITION = 2;
    /**
     * Метод отвечающий за сериализацию объекта
     * @param object объект
     * @param <T> тип объект
     * @return byte[] массив байтов
     * Метод использует классы ByteArrayOutputStream и ObjectOutputStream.
     * Класс ObjectOutputStream записывает объект в сериализованном виде в поток ByteArrayOutputStream
     * Далее применяя byteArrayOutputStream.toByteArray() можно получить массив байтов, что нам и нужнодля общения по UDP
     */

    public static <T> byte[] SerializeObject(T object, String login, String password){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            //System.out.println(object);
            objectOutputStream.writeUTF(login);
            objectOutputStream.writeUTF(password);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Возникла ошибка сериализации");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод, использующийся для десериализации сообщений пользователя
     * Каждое сообщение содержит в себе login, password и Object(product/command)
     * @param buffer буффер, из которого считывается информация
     */
    public static  <T>  Object[] DeserializeObject(byte[] buffer){
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            Object[] array = new Object[3];
            array[LOGIN_POSITION] = objectInputStream.readUTF();
            array[PASSWORD_POSITION] = objectInputStream.readUTF();
            array[OBJECT_POSITION] = (T) objectInputStream.readObject();
            return array;
        } catch (EOFException e){
            e.printStackTrace();
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Возникла ошибка десериализации");
            e.printStackTrace();
        }
        return null;
    }


}
