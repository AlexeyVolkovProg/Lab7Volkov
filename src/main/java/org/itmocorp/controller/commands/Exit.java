package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;

public class Exit extends AbstractCommand{

    public Exit(){
        name = "exit";
        help = "Завершает выполнение программы";
    }

    @Override
    public void execute(CommandManager CM) {
        CM.setRunning(false);
    }
}
