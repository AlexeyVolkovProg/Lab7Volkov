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
import java.util.PrimitiveIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataBaseManager implements Serializable {
    private static String URL; // адрес DataBase
    private static String login;
    private static String password;

    public static void setArgs(String[] properties) {
        URL = properties[Server.DB_URL_POSITION];
        login = properties[Server.DB_USERNAME_POSITION];
        password = properties[Server.DB_PASSWORD_POSITION];

        try {
            Class.forName("org.postgresql.Driver"); // проверяет установлен ли драйвер в проекте
            System.out.println("Драйвер подключен");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver не найден.");
            System.exit(1);
        }
        try (Connection connection = DriverManager.getConnection(DataBaseManager.URL, DataBaseManager.login, DataBaseManager.password)) {
            System.out.println("Подключение успешно");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(login TEXT, password TEXT)").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS products(id integer, creation_date TEXT, name TEXT, coordinateX bigint, coordinateY integer, price bigint, manufactureCost bigint, unitOfMeasure TEXT, personName TEXT, personBirthday TEXT, personPassportId TEXT, login TEXT)").execute();
        } catch (SQLException e) {
            System.out.println("Ошибка подключения");
            System.exit(1);

        }

    }

    /**
     * Метод, который проверяет авторизацию пользователя
     * @param login логин
     * @param password пароль
     * @return true если данные прошли идентификацию, в противном случае false
     */
    public static boolean login(String login, String password) {
        try (Connection connection = DriverManager.getConnection(DataBaseManager.URL, DataBaseManager.login, DataBaseManager.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String pass = resultSet.getString("password"); // достаем пароль из базы данных
                try {
                    String encryptedPassword = encryptionMD5(password);
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
            System.out.println("Ошибка подключения");
            System.exit(2);
            return false;
        }
    }

    /**
     * Шифрование пароля по алгоритму MD5
     * @param password пароль
     * @return пароль после хеширования
     */
    private static String encryptionMD5(String password) throws NoSuchAlgorithmException {
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
        return sb.toString();
    }

    /**
     * Метод добавления нового пользователя в БД
     * @param login логин
     * @param password пароль
     * @return true, при успешном добавлении, в противном случае false
     */

    public static boolean addUser(String login, String password) {
        try (Connection connection = DriverManager.getConnection(DataBaseManager.URL, DataBaseManager.login, DataBaseManager.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) { // с такими данными есть пользователь, поэтому в регистрации отказано
               return false;
            }
            else {
                String encryptedPassword;
                try {
                    encryptedPassword = encryptionMD5(password);
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
            System.out.println("Ошибка подключения");
            System.exit(3);
            return false;
        }
    }

    /**
     * Обновляет коллекцию, подгружая данных из памяти БД
     *
     */
    public static void updateCollectionFromDataBase() {
        synchronized (CommandManager.collection) {
            CopyOnWriteArrayList<Product> Products = new CopyOnWriteArrayList<>();
            try (Connection connection = DriverManager.getConnection(DataBaseManager.URL, DataBaseManager.login, DataBaseManager.password)) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM products");
                while (resultSet.next()) {
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
                Product.setIdSetter(maxId + 1);
            } catch (SQLException e) {
                System.out.println("Ошибка подключения");
                System.exit(4);
            }
        }
    }

    /**
     * Обновляем данные в БД, выгружая их из коллекции
     *
     * @return false, если не удалось
     */
    public static boolean updateDataBase() {
        try (Connection connection = DriverManager.getConnection(DataBaseManager.URL, DataBaseManager.login, DataBaseManager.password)) {
            connection.prepareStatement("DELETE FROM products").executeUpdate();
            for (Product product: CommandManager.collection) {
                addToDataBase(product);
            }
            return true;
        } catch (SQLException e) {
            System.exit(5);
            return false;
        }
    }


    /**
     * Метод добавляет элемент в БД
     *
     * @param product элемент
     * @return false, если не удалось
     */
    public static boolean addToDataBase(Product product) {
        try (Connection connection = DriverManager.getConnection(DataBaseManager.URL, DataBaseManager.login, DataBaseManager.password)) {
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM products");
//            int maxId = 0;
//            while (resultSet.next()) {
//                maxId = resultSet.getInt("max");
//            }
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO products(id, creation_date, name, coordinateX, coordinateY, price, manufactureCost, unitOfMeasure, personName, personBirthday, personPassportId, login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setInt(1, product.getId());//Integer.toString(product.getId()));
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
            preparedStatement.setString(12, product.getLogin());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.exit(6);
            return false;
        }
    }

}
