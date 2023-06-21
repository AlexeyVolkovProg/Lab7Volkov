package org.itmocorp;


import org.itmocorp.controller.commands.AbstractCommand;
import org.itmocorp.controller.commands.Login;
import org.itmocorp.controller.commands.Register;
import org.itmocorp.controller.handlers.InputHandler;
import org.itmocorp.controller.handlers.ScriptHandler;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс клиента Client
 *
 * @author Alexey Volkov P3113
 */
public class Client {
    private static DatagramChannel datagramChannel;
    private static SocketAddress socketAddress;
    private final Selector selector;
    ArrayList<AbstractCommand> arrayList = new ArrayList<>(); // очередь команд
    ArrayList<Product> arrayListPr = new ArrayList<>(); // очередь отправляющихся объектов Product, для команд

    public CommandManager commandManager = new CommandManager();
    private boolean auth = false; // флаг, отвечающий за статус авторизации
    private String login = "";
    private String password = "";


    public Client() throws IOException {
        selector = Selector.open();
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
    }

    /**
     * Класс main
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            Client client = new Client();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // Код, который нужно выполнить при завершении работы программы
                System.out.println("Клиент завершает свою работу");
            }));
            client.connect("localhost", 1555);
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Пытаетеся присоединиться к серверу
     *
     * @param hostname имя сервера
     * @param port     порт
     * @throws IOException IOException
     */

    private void connect(String hostname, int port) throws IOException {
        socketAddress = new InetSocketAddress(hostname, port);
        datagramChannel.connect(socketAddress);
        System.out.println("Устанавливаем соединение с " + hostname + " по порту " + port);
    }

    /**
     * Получает ответ от сервера
     *
     * @return полученное сообщение
     * @throws IOException IOException
     */
    private String receiveAnswer() throws IOException {
        byte[] bytes = new byte[1000000];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketAddress = datagramChannel.receive(buffer);
        return new String(buffer.array()).split("�")[0].trim();
    }


    /**
     * Отправляет команду серверу
     *
     * @param command передаваемая команда
     * @throws IOException IOException
     */

    public void sendCommand(AbstractCommand command) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(Serialization.SerializeObject(command, login, password));
        datagramChannel.send(buffer, socketAddress);
        if (command != null)
            if (command.getClass().getName().contains("Exit"))
                System.exit(0);
    }


    /**
     * Отправляет объект Product серверу, как дополнительный аргумент для некоторых команд
     *
     * @param product объект класса Product
     * @throws IOException IOException
     */
    public void sendProduct(Product product) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(Serialization.SerializeObject(product, login, password));
        datagramChannel.send(buffer, socketAddress);
    }


    /**
     * Метод для добавления команд в очередь
     */
    public void addCommand(AbstractCommand command) {
        arrayList.add(command);
    }

    /**
     * Метод для добавления продуктов в очередь
     */
    public void addProduct(Product product) {
        product.setLogin(login);
        arrayListPr.add(product);
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (!auth) // запускаем блок авторизации пользователя
                authorization(scanner);
            datagramChannel.register(selector, SelectionKey.OP_WRITE);
            while (selector.select(15000) > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        String answer = receiveAnswer();
                        if (answer.contains("Конец ввода"))
                            datagramChannel.register(selector, SelectionKey.OP_WRITE);
                        else System.out.println(answer);
                    }
                    if (selectionKey.isWritable()) {
                        datagramChannel.register(selector, SelectionKey.OP_READ);
                        if (arrayList.size() == 0) {
                            AbstractCommand command = CommandManager.CommandDeterminator(scanner.nextLine().trim().split("\\s+"));
                            if (command != null && command.getName().equals("executeScript")) {   // команда executeScript будет иметь свою логику(из нее будут доставаться команды и далее посылаться)
                                if (command.getArgs().length != 0) {
                                    ScriptHandler.startFile(command.getArgs()[0], this);  // считываем команды из скрипта и внутри ScriptHandlera
                                } else {
                                    System.out.println("Данная команда требует указание пути к файлу исполняемого скрипта.");
                                }
                                if (arrayList.size() == 0)
                                    datagramChannel.register(selector, SelectionKey.OP_WRITE);
                            } else {
                                addCommand(command);
                                if (command != null && command.isNeedObjectToExecute()) {
                                    addProduct(InputHandler.ArgumentsReader());//CM.getProduct());
                                }
                            }
                        }
                        if (arrayList.size() > 0) {
                            AbstractCommand current = arrayList.get(0);
                            sendCommand(current);
                            if (current != null && current.isNeedObjectToExecute()) {
                                sendProduct(arrayListPr.get(0));
                                arrayListPr.remove(0);
                            }
                            arrayList.remove(0);
                        }
                    }
                }
            }
        } catch (PortUnreachableException e) {
            System.out.println("Не удалось получить данные по указанному порту/сервер не доступен");
            run();
        } catch (NoSuchElementException e) {
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Превышено время ожидания от сервера");
        arrayList.clear();
        arrayListPr.clear();
        run();
    }


    /**
     * Метод отвечающий за авторизацию пользователя
     *
     * @param scanner сканнер, привязанный к консоли пользователя
     */
    private void authorization(Scanner scanner) {
        try {
            String[] inputStringArray;
            String inputString = null;
            boolean authFlag = true;
            do {
                System.out.println("Если вы уже зарегистрированы, введите login для входа, иначе введите register");
                inputStringArray = scanner.nextLine().trim().split("\\s+");
                if (inputStringArray.length > 1) {
                    System.out.println("Команда не должна содержать аргументы. Повторите ввод");
                    authFlag = false;
                }else if (inputStringArray[0].equals("register") || inputStringArray[0].equals("login")){
                    inputString = inputStringArray[0];
                    authFlag = true;
                }else{
                    System.out.println("Команда не найдена. Введите register/login");
                }
            }
            while (!authFlag);
            if (inputString.equals("register"))
                register(scanner);
            else
                login(scanner);
            String answer = "";
            while (answer.isEmpty()) {
                answer = receiveAnswer();
                if (answer.equals("Пользователь успешно вошёл в систему."))
                    auth = true;
            }
            System.out.println(answer);
        } catch (Exception e) {
            System.err.println("Ошибка авторизации");
        }
    }

    /**
     * Блок регистрации пользователя
     *
     * @param scanner сканер для ввода данных пользователем
     */
    private void register(Scanner scanner) throws IOException {
        login = "";
        boolean lessThen4;
        boolean withSpaces;
        boolean invalidChars;
        do {
            System.out.println("Придумайте логин, содержащий не менее 4 символов (допускается использование только английских прописных букв и цифр) ");
            login = scanner.nextLine();
            lessThen4 = login.trim().split("\\s+")[0].length() < 4;
            withSpaces = login.trim().split("\\s+").length != 1;
            invalidChars = !login.trim().split("\\s+")[0].matches("[a-z0-9]+");
        } while (lessThen4 || withSpaces || invalidChars);
        password = "";
        do {
            System.out.println("Придумайте пароль, содержащий не менее 4 (допускается использование только английских прописных букв и цифр)");
            password = scanner.nextLine();
            lessThen4 = password.trim().split("\\s+")[0].length() < 4;
            withSpaces = password.trim().split("\\s+").length != 1;
            invalidChars = !password.trim().split("\\s+")[0].matches("[a-z0-9]+");
        } while (lessThen4 || withSpaces || invalidChars);
        System.out.println("Ваш логин: " + login.trim().split("\\s+")[0] + "\nВаш пароль: " + password.trim().split("\\s+")[0]);
        sendCommand(new Register());
    }

    /**
     * Блок входа пользователя
     * Отправка команды Login на сервер
     *
     * @param scanner сканер для ввода данных пользователем
     */
    private void login(Scanner scanner) throws IOException {
        System.out.print("Введите логин: ");
        login = scanner.nextLine();
        System.out.print("Введите пароль: ");
        password = scanner.nextLine();
        sendCommand(new Login());
    }
}
