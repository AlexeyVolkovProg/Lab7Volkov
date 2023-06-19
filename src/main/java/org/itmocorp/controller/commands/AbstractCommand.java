package org.itmocorp.controller.commands;

import org.itmocorp.controller.managers.CommandManager;
import org.itmocorp.model.data.Product;

import java.io.Serializable;

public abstract class AbstractCommand implements Serializable {
    public String name;
    protected String help;
    protected String[] args; // аргументы реализованы, как поле, чтобы легко их передавать с клиента
    protected boolean needObjectToExecute = false;

    protected Product product = null;

    public abstract void execute(CommandManager CM);

    public String getHelp() {
        return help;
    }

    public String getName() {
        return name;
    }

    public boolean isNeedObjectToExecute() {
        return needObjectToExecute;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }
}
