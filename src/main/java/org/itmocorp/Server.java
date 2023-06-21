package org.itmocorp;

import org.itmocorp.controller.commands.AbstractCommand;
import org.itmocorp.controller.commands.Save;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс Server, отвечает за серверную часть нашего приложения
 *
 * @author Alexey Volkov, P3113
 */

public class Server {
    private DatagramChannel datagramChannel; // канал для обмена информацией
    private AbstractCommand command = new AbstractCommand() {
        @Override
        public void execute(CommandManager CM) {
        }
    };
    private static final int port = 1555;
    // Создание ThreadPool'ов
    private final ExecutorService requestProcessingPool = Executors.newFixedThreadPool(10);

    public static final int DB_URL_POSITION = 0;
    public static final int DB_USERNAME_POSITION = 1;
    public static final int DB_PASSWORD_POSITION = 2;


    /**
     * Метод main
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Запускаем работу сервера по порту " + port);
        if (args.length == 0) {
            System.out.println("Сервер требует аргумент конфигурационного файла для входа в БД");
            System.exit(0);
            return;
        } else {
            String[] properties = readPropeties(args[0]);
            if (properties.length < 3) {
                System.out.println("Проверьте файл");
                System.exit(0);
                return;
            }
            DataBaseManager.setArgs(properties);
        }
        server.run();
    }

    public static String[] readPropeties(String configFile) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            String[] propertiesArray = new String[3];
            propertiesArray[DB_URL_POSITION] = properties.getProperty("db.url");
            propertiesArray[DB_USERNAME_POSITION] = properties.getProperty("db.username");
            propertiesArray[DB_PASSWORD_POSITION] = properties.getProperty("db.password");
            return propertiesArray;
        } catch (IOException e) {
            System.out.println("Файл не был найден или нет доступа к нему");
            return new String[0];
        }
    }

    /**
     * Метод receive позволяет получать и обрабатывать сообщения от клиента
     */
    private void receive() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        byteBuffer.clear();
        SocketAddress socketAddress = datagramChannel.receive(byteBuffer);  // получаем адрес сокета, с которого была получена информация, null в случае если нет доступных данных для чтения
        byteBuffer.flip();   // переключаем buffer в режим записи
        if (socketAddress != null) {
//            new Thread(() -> {  // запуск потока для обработки сообщения клиента
            try {
                Object[] arrayUserMessage = Serialization.DeserializeObject(byteBuffer.array()); // считываем массив пришедшей информации от пользователя
                String login = (String) arrayUserMessage[Serialization.LOGIN_POSITION];
                String password = (String) arrayUserMessage[Serialization.PASSWORD_POSITION];
                Object object = arrayUserMessage[Serialization.OBJECT_POSITION];
                if (object == null) {
                    datagramChannel.send(ByteBuffer.wrap("Команда была не найдена или же вы ввели неверное кол-во аргументов. Введите help для просмотра команд".getBytes()), socketAddress);
                    datagramChannel.send(ByteBuffer.wrap("Конец ввода".getBytes()), socketAddress);
                    return;
                }
                if (object.getClass().getName().contains(".Login"))
                    authorization("login", login, password, socketAddress);
                else if (object.getClass().getName().contains(".Register"))
                    authorization("register", login, password, socketAddress);
                else if (!object.getClass().getName().contains(".Product")) {
                    if (!DataBaseManager.login(login, password)) {
                        datagramChannel.send(ByteBuffer.wrap("Авторизация не прошла, команды не будут выполнены".getBytes()), socketAddress);
                        datagramChannel.send(ByteBuffer.wrap("Конец ввода".getBytes()), socketAddress);
                        return;
                    }

                    command = (AbstractCommand) object;

                    System.out.println(" Сервер получил команду: " + command.getName());
                    if (!command.isNeedObjectToExecute()) {
                        //Команда не требует объекта для выполнения. Начинаем выполнение
                        requestProcessingPool.submit(() -> {
                            CommandManager CM = new CommandManager();
                            CM.setLogin(login);
                            try {
                                CM.ExecuteCommand(command, datagramChannel, socketAddress);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } else if (command != null) {
                    Product product = new Product((Product) object);
                    if (!DataBaseManager.login(login, password)) {
                        datagramChannel.send(ByteBuffer.wrap("Авторизация не прошла, команды не будут выполнены".getBytes()), socketAddress);
                        datagramChannel.send(ByteBuffer.wrap("Конец ввода".getBytes()), socketAddress);
                        return;
                    }
//                        System.out.println(product);//lrlflflflfflfl
                    command.setProduct(product);
                    requestProcessingPool.submit(() -> {  // пул потоков для обработки выполнения команд
                        CommandManager CM = new CommandManager();
                        CM.setLogin(login);
                        try {
                            CM.ExecuteCommand(command, datagramChannel, socketAddress);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            }).start();
        }
    }


    /**
     * Авторизация пользователя
     *
     * @param message  вид авторизации
     * @param login    логин
     * @param password пароль
     * @throws IOException исключение
     */
    private void authorization(String message, String login, String password, SocketAddress socketAddress) throws IOException {
        if (message.equals("login")) {
            if (DataBaseManager.login(login, password)) {
                datagramChannel.send(ByteBuffer.wrap("Пользователь успешно вошёл в систему.".getBytes()), socketAddress);

            } else datagramChannel.send(ByteBuffer.wrap("Не удалось войти в систему.".getBytes()), socketAddress);
        }
        if (message.equals("register")) {
            if (DataBaseManager.addUser(login, password))
                datagramChannel.send(ByteBuffer.wrap("Пользователь добавлен. Войдите в систему.".getBytes()), socketAddress);
            else
                datagramChannel.send(ByteBuffer.wrap("Не удалось добавить пользователя. Логин занят, содержит недопустимые символы или их последовательность. Мне наплевать, если честно, сами гуглите, какой логин и пароль захавает чёртов postgresql".getBytes()), socketAddress);
        }
    }


    /**
     * Метод, проверяющий консоль сервера на поступление команды Save(единственная команда доступная серверу)
     */
    public void checkServerCommand() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                if (scanner.nextLine().trim().equals("save")) {
                    Save save = new Save();
                    save.setArgs(new String[0]);
                    save.execute(new CommandManager());
                } else {
                    System.out.println("Введена неверная серверная команда");
                }
            } catch (NoSuchElementException e) { // проверка на символ остановки
                System.exit(-1);
                return;
            }
        }
    }


    /**
     * Класс, отвечающий за работу сервера
     */
    public void run() {
        SocketAddress socketAddress = new InetSocketAddress(port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {  // Код, который нужно выполнить при завершении работы cервера
            Save save = new Save();
            save.setArgs(new String[0]);
            save.execute(new CommandManager());
            System.out.println("Сервер завершает свою работу");
        }));
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.bind(socketAddress);
            datagramChannel.configureBlocking(false);
            DataBaseManager.updateCollectionFromDataBase();
            new Thread(this::checkServerCommand).start(); // старт отдельного потока, который отвечает за проверку серверной консоли на ввод команды save
            while (true) {
                receive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
