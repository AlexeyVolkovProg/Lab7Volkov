package org.itmocorp;

import org.itmocorp.controller.commands.AbstractCommand;
import org.itmocorp.controller.commands.Save;
import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;
import org.itmocorp.model.managers.CollectionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.text.DateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Класс Server, отвечает за серверную часть нашего приложения
 *
 * @author Alexey Volkov, P3113
 */
public class Server {

    private static Logger LoggerFactory;
    private static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
    private DatagramChannel datagramChannel; // канал для обмена информацией
    private SocketAddress socketAddress; // адрес определенного сокета
    private AbstractCommand command = null;
    private String[] args;

    private static Date creationDate; // дата, пускай будет на всякий


    private DataBaseManager dataBaseManager = new DataBaseManager();

    private static int port = 1555;

    // Создание ThreadPool'ов
    private ExecutorService requestProcessingPool = Executors.newFixedThreadPool(10);


    /**
     * Метод main
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.setArgs(args);
        System.out.println("Запускаем работу сервера по порту " + port);
        server.run();
    }

    /**
     * Метод receive позволяет получать и обрабатывать сообщения от клиента
     */
    private void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        byteBuffer.clear();
        SocketAddress socketAddress1;
        try {
            socketAddress1 = datagramChannel.receive(byteBuffer);  // получаем адрес сокета, с которого была получена информация, null в случае если нет доступных данных для чтения
        } catch (IOException e) {
            socketAddress1 = null;
        }
        byteBuffer.flip();   // переключаем buffer в режим записи

        if (socketAddress1 != null) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
                 ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                String login = objectInputStream.readUTF();
                String password = objectInputStream.readUTF();
                Object object = objectInputStream.readObject();
                if (object == null) {
                    //Получена несуществующая команда
                    datagramChannel.send(ByteBuffer.wrap("Команда была не найдена или же вы ввели неверное кол-во аргументов. Введите help для просмотра команд".getBytes()), socketAddress1);
                    datagramChannel.send(ByteBuffer.wrap("Конец ввода".getBytes()), socketAddress1);
                    return;
                }
                if (object.getClass().getName().contains(".Login"))
                    authorization("login", login, password, socketAddress1);
                else if (object.getClass().getName().contains(".Register"))
                    authorization("register", login, password, socketAddress1);
                else if (!object.getClass().getName().contains(".Product")) {
                    command = (AbstractCommand) object;
                    System.out.println(" Сервер получил команду: " + command.getName());
                    if (!command.isNeedObjectToExecute()) {
                        //Команда не требует объекта для выполнения. Начинаем выполнение
                        SocketAddress finalSocketAddress = socketAddress1;
                        requestProcessingPool.submit(() -> {
                            CommandManager CM = new CommandManager();
                            try {
                                CM.ExecuteCommand(command, datagramChannel, finalSocketAddress);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } else if (command != null) {
                    Product product = new Product((Product) object);
                    command.setProduct(product);
                    SocketAddress finalSocketAddress1 = socketAddress1;
                    requestProcessingPool.submit(() -> {
                        CommandManager CM = new CommandManager();
                        try {
                            CM.ExecuteCommand(command, datagramChannel, finalSocketAddress1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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
            //logger.info("Команда входа в систему определена");
            if (dataBaseManager.login(login, password)) {
                datagramChannel.send(ByteBuffer.wrap("Пользователь успешно вошёл в систему.".getBytes()), socketAddress);

            } else datagramChannel.send(ByteBuffer.wrap("Не удалось войти в систему.".getBytes()), socketAddress);
        }
        if (message.equals("register")) {
            //logger.info("Команда регистрации в системе определена");

            if (dataBaseManager.addUser(login, password))
                datagramChannel.send(ByteBuffer.wrap("Пользователь добавлен. Войдите в систему.".getBytes()), socketAddress);
            else
                datagramChannel.send(ByteBuffer.wrap("Не удалось добавить пользователя. Логин занят, содержит недопустимые символы или их последовательность. Мне наплевать, если честно, сами гуглите, какой логин и пароль захавает чёртов postgresql".getBytes()), socketAddress);
        }
    }


    /**
     * Метод проверяющий консоль сервера на поступление команды Save(единственная команда доступная серверу
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

    public void run() {

        if (args.length == 0) {
            System.out.println("Не был указан файл");
        } else {
            String filePath = args[0];
            String fileSeparator = ",";
            CollectionManager collectionManager1 = new CollectionManager(filePath, fileSeparator);
            socketAddress = new InetSocketAddress(port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // Код, который нужно выполнить при завершении работы программы
                Save save = new Save();
                save.setArgs(new String[0]);
                save.execute(new CommandManager());
                System.out.println("Сервер завершает свою работу");
            }));
            try {
                datagramChannel = DatagramChannel.open();
                datagramChannel.bind(socketAddress);
                datagramChannel.configureBlocking(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(this::checkServerCommand).start();
            while (true) {
                new Thread(this::receive).start();
            }
        }
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
