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

    public <T> byte[] SerializeObject(T object){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            //System.out.println(object);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Возникла ошибка сериализации");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param buffer массив байтов
     * @param <T> тип объекта, который хотим десериализовать
     * @return объект, который был получен после десериализации
     * Метод использует классы ObjectInputStream, для того чтобы из ByteArrayInputStream получить десериализованный объект
     */

    public <T> T DeserializeObject(byte[] buffer){
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            return (T) objectInputStream.readObject();
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
