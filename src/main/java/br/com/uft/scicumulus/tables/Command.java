/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.tables;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author fredsilva
 */
public class Command {
    private StringProperty command;

    public Command(String command) {
        this.command = new SimpleStringProperty(command);
    }

    public StringProperty getCommand() {
        return command;
    }
    
    
}
