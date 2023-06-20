package org.itmocorp;

import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Coordinates;
import org.itmocorp.model.data.Person;
import org.itmocorp.model.data.Product;
import org.itmocorp.model.data.UnitOfMeasure;

import java.io.Serializable;
import java.sql.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataBaseManager implements Serializable {
    private final String URL = "jdbc:postgresql://localhost:5431/studs";

    private String login = "s367140";
    private String password = "eBNuRjEouV14Z1uN";
    public DataBaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер подключен");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver не найден.");
            System.exit(1);
            //e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(URL, login, password)) {
            System.out.println("Подключение успешно");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(login TEXT, password TEXT)").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS products(id integer, creation_date TEXT, name TEXT, coordinateX bigint, coordinateY integer, price bigint, manufactureCost bigint, unitOfMeasure TEXT, personName TEXT, personBirthday TEXT, personPassportId TEXT, login TEXT)").execute();
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Ошибка подключения");
            System.exit(1);

        }
    }

    public boolean login(String login, String password) {
        try (Connection connection = DriverManager.getConnection(URL, this.login, this.password)) {
            //System.out.println("Подключение успешно");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String pass = resultSet.getString("password");
                try {
                    // Создание экземпляра MessageDigest с алгоритмом MD5
                    MessageDigest md = MessageDigest.getInstance("MD5");

                    // Конвертация строки в байтовый массив
                    byte[] passwordBytes = password.getBytes();

                    // Вычисление хэш-суммы MD5
                    byte[] digest = md.digest(passwordBytes);

                    // Конвертация байтового массива в строку в шестнадцатеричном формате
                    StringBuilder sb = new StringBuilder();
                    for (byte b : digest) {
                        sb.append(String.format("%02x", b));
                    }

                    String encryptedPassword = sb.toString();

                    return encryptedPassword.equals(pass);
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Алгоритм шифрования MD5 не найден.");
                    e.printStackTrace();
                    return false;
                }
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Ошибка подключения");
            System.exit(2);
            return false;
        }
    }

    public boolean addUser(String login, String password) {
        try (Connection connection = DriverManager.getConnection(URL, this.login, this.password)) {
            //System.out.println("Подключение успешно");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
               return false;
            }
            else {
                String encryptedPassword;
                try {
                    // Создание экземпляра MessageDigest с алгоритмом MD5
                    MessageDigest md = MessageDigest.getInstance("MD5");

                    // Конвертация строки в байтовый массив
                    byte[] passwordBytes = password.getBytes();

                    // Вычисление хэш-суммы MD5
                    byte[] digest = md.digest(passwordBytes);

                    // Конвертация байтового массива в строку в шестнадцатеричном формате
                    StringBuilder sb = new StringBuilder();
                    for (byte b : digest) {
                        sb.append(String.format("%02x", b));
                    }

                    encryptedPassword = sb.toString();
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Алгоритм шифрования MD5 не найден.");
                    e.printStackTrace();
                    return false;
                }
                PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO users(login, password) VALUES (?, ?)");
                preparedStatement1.setString(1, login);
                preparedStatement1.setString(2, encryptedPassword);
                preparedStatement1.executeUpdate();

                return true;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Ошибка подключения");
            System.exit(3);
            return false;
        }
    }

    /**
     * Обновляет коллекцию в памяти из БД
     *
     */
    public void updateCollectionFromDataBase() {
        //logger.info("Пытаемся обновить коллекцию в памяти");
        CopyOnWriteArrayList<Product> Products = new CopyOnWriteArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, this.login, this.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM products");
            while (resultSet.next()) {
//                connection.prepareStatement("CREATE TABLE IF NOT EXISTS products(id integer, creation_date TEXT, name TEXT, coordinateX bigint, coordinateY integer, price bigint, manufactureCost bigint, unitOfMeasure TEXT, personName TEXT, personBirthday TEXT, personPassportId TEXT, login TEXT)").execute();
                Product product = new Product(resultSet.getInt("id"), ZonedDateTime.parse(resultSet.getString("creation_date"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z")), resultSet.getString("name"), new Coordinates(resultSet.getLong("coordinateX"), resultSet.getInt("coordinateY")), resultSet.getLong("price"), resultSet.getLong("manufactureCost"), UnitOfMeasure.valueOf(resultSet.getString("unitOfMeasure")), new Person(resultSet.getString("personName"), resultSet.getString("personBirthday"), resultSet.getString("personPassportId")), resultSet.getString("login"));
                Products.add(product);
            }
            CommandManager.collection.clear();
            CommandManager.collection.addAll(Products);
            Statement statement1 = connection.createStatement();
            ResultSet resultSet1 = statement1.executeQuery("SELECT MAX(id) FROM products");
            int maxId = -1;
            while (resultSet1.next()) {
                maxId = resultSet1.getInt("max");
            }
            Product.setIdSetter(maxId+1);
            //logger.info("Коллекция обновлена");
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Ошибка подключения");
            System.exit(4);
            //logger.error("Не удалось обновить коллекцию в памяти");
        }
    }

    /**
     * Обновляет элементы коллекции
     *
     * @return false, если не удалось
     */
    public boolean updateDataBase() {
        try (Connection connection = DriverManager.getConnection(URL, this.login, this.password)) {
            //logger.info("Пытаемся обновить объект в базе данных");
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM products");
            preparedStatement.executeUpdate();
            for (Product product: CommandManager.collection) {
                if (addToDataBase(product, product.getLogin())) {
                    //logger.info("Объект успешно обновлён");
                }
            }
        } catch (SQLException e) {
            System.exit(5);
            //e.printStackTrace();
            //logger.error("Не удалось обновить элемент");
            return false;
        }
        return true;
    }


    /**
     * Добавляет элемент в БД
     *
     * @param product элемент
     * @return false, если не удалось
     */
    public boolean addToDataBase(Product product, String login) {
        try (Connection connection = DriverManager.getConnection(URL, this.login, this.password)) {
            //logger.info("Пытаемся добавить объект в базу данных");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM products");
            int maxId = 0;
            while (resultSet.next()) {
                maxId = resultSet.getInt("max");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO products(id, creation_date, name, coordinateX, coordinateY, price, manufactureCost, unitOfMeasure, personName, personBirthday, personPassportId, login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setInt(1, maxId + 1);//Integer.toString(product.getId()));
            preparedStatement.setString(2, product.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z")));
            preparedStatement.setString(3, product.getName());
            preparedStatement.setLong(4, product.getCoordinates().getX());
            preparedStatement.setInt(5, product.getCoordinates().getY());
            preparedStatement.setLong(6, product.getPrice());
            preparedStatement.setLong(7, product.getManufactureCost());
            preparedStatement.setString(8, product.getUnitOfMeasure().toString());
            preparedStatement.setString(9, product.getOwner().getName());
            preparedStatement.setString(10, product.getOwner().getBirthday());
            preparedStatement.setString(11, product.getOwner().getPassportID());
            preparedStatement.setString(12, login);
            preparedStatement.executeUpdate();
            //System.out.println(product + " после");
            //logger.info("Объект добавлен в БД");
            return true;
        } catch (SQLException e) {
            System.exit(6);
            //e.printStackTrace();
            //logger.error("Не удалось добавить объект в БД");
            return false;
        }
    }

}
