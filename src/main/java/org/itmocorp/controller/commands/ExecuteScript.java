package org.itmocorp.controller.commands;

import org.itmocorp.controller.handlers.ScriptHandler;
import org.itmocorp.controller.managers.CommandManager;

import java.io.File;


public class ExecuteScript extends AbstractCommand {

    public ExecuteScript(){
        name = "executeScript";
        help = "Считывает и исполняет скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.";
    }

    @Override
    public void execute(CommandManager CM) { }
}
