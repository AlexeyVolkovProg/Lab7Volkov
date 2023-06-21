package org.itmocorp.controller.managers;

import org.itmocorp.controller.commands.*;
import org.itmocorp.model.data.Product;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.*;

public class CommandManager {

    public static Map<String, AbstractCommand> commands = new HashMap<>(); // коллекция, которая содержит все доступные нам команды

    // Потокобезопасная коллекция
    public final static List<Product> collection = new CopyOnWriteArrayList<>(); // коллекция, с которой идет работа

    //public CommandManager commandManager = new CommandManager(); // создаем объект класса CommandManager в самом нем
    public static Date collectionTime;
    private boolean isRunning = true; // показывает статус работы программы
    private boolean ScriptStatus = false; // показывает работает ли сейчас скрипт или нет

    //далее идут поля добавленные для работы с серверной частью

    private DatagramChannel serverDatagramChannel; // поле, хранящее DatagramChannel для общения с клиентом

    private SocketAddress socketAddress;


    public Map<String, AbstractCommand> getCommands() {
        return commands;
    }

    private static ExecutorService responseSendingPool = Executors.newCachedThreadPool();

    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public CommandManager() {
        collectionTime = new Date();
        commands.put("add", new Add());
        commands.put("executeScript", new ExecuteScript());
        commands.put("show", new Show());
        commands.put("updateId", new UpdateId());
        commands.put("addIfMax", new AddIfMax());
        commands.put("groupCountingById", new GroupCountingById());
        commands.put("countGreaterThanUnitOfMeasure", new CountGreaterThanUnitOfMeasure());
        commands.put("exit", new Exit());
        commands.put("removeFirst", new RemoveFirst());
        commands.put("removeById", new RemoveById());
        commands.put("info", new Info());
        //commands.put("save", new Save());
        commands.put("clear", new Clear());
        commands.put("printDescending", new PrintDescending());
        commands.put("help", new Help());
        commands.put("shuffle", new Shuffle());
    }


    /**
     * Статический метод исполнения команды, полученной от клиента
     *
     * @param command         команда полученная от клиента
     * @param datagramChannel канал, использующийся для передачи сообщений клиенту
     * @param socketAddress   адрес порта
     */

    public void ExecuteCommand(AbstractCommand command, DatagramChannel datagramChannel, SocketAddress socketAddress) throws IOException {
        this.setServerDatagramChannel(datagramChannel);
        this.setSocketAddress(socketAddress);
        command.execute(this);
        responseSendingPool.submit(() -> {
            try {
                datagramChannel.send(ByteBuffer.wrap("Конец ввода".getBytes()), socketAddress);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * Метод помогает клиенту определить введенную команду
     * @param args
     * @return AbstractCommand
     */
    public static AbstractCommand CommandDeterminator(String[] args) {
        String cmd = args[0].trim();
        args = Arrays.copyOfRange(args, 1, args.length);
        AbstractCommand command = commands.get(cmd);
        if (command != null) {
            command.setArgs(args);
            return command;
        }
        return null;
    }


    /**
     * Метод использующийся, для создания объект при помощи пользовательского ввода от клиента
     * @return product
     */
//    public Product getProduct() {
//        Product product;
//        if (isScriptStatus())
//            product = ScriptHandler.getProductFromFile();
//        else
//            product = InputHandler.ArgumentsReader();
//        return product;
//    }

    /**
     * Статический метод отвечающий за отправку сообщений клиенту
     *
     * @param line
     */
    public void printToClient(String line) {
        try {
            Thread.sleep(3);
            ByteBuffer buffer = ByteBuffer.wrap((line.getBytes()));
            this.getServerDatagramChannel().send(buffer, this.getSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void setServerDatagramChannel(DatagramChannel serverDatagramChannel) {
        this.serverDatagramChannel = serverDatagramChannel;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setScriptStatus(boolean scriptStatus) {
        ScriptStatus = scriptStatus;
    }

    public boolean isScriptStatus() {
        return ScriptStatus;
    }

    public DatagramChannel getServerDatagramChannel() {
        return serverDatagramChannel;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
