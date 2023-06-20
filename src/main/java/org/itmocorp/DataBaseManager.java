package org.itmocorp;

import java.io.Serializable;
import java.sql.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(URL, login, password)) {
            System.out.println("Подключение успешно");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(login TEXT, password TEXT)").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS products(id integer, creation_date TEXT, name TEXT, coordinateX bigint, coordinateY integer, price bigint, manufactureCost bigint, unitOfMeasure TEXT, personName TEXT, personBirthday TEXT, personPassportId TEXT, login TEXT)").execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка подключения");
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
            e.printStackTrace();
            System.out.println("Ошибка подключения");
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
            e.printStackTrace();
            System.out.println("Ошибка подключения");
            return false;
        }
    }
}
