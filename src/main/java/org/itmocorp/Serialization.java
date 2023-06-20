package org.itmocorp;

import java.io.*;

/**
 * Класс, который отвечает на сериализацию и десериализацию объектов
 *
 * @author Alexey Volkov, P3113
 */

public class Serialization {
    /**
     * Метод отвечающий за сериализацию объекта
     * @param object объект
     * @param <T> тип объект
     * @return byte[] массив байтов
     * Метод использует классы ByteArrayOutputStream и ObjectOutputStream.
     * Класс ObjectOutputStream записывает объект в сериализованном виде в поток ByteArrayOutputStream
     * Далее применяя byteArrayOutputStream.toByteArray() можно получить массив байтов, что нам и нужнодля общения по UDP
     */

    public <T> byte[] SerializeObject(T object, String login, String password){
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
}
