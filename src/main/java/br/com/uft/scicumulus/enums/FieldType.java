/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.enums;

import br.com.uft.scicumulus.interfaces.FormsInterface;
import fxml.FormTypeFileController;

/**
 *
 * @author:
 * Thaylon Guedes Santos
 * Frederico da Silva Santos
 */
public enum FieldType {
    
    FLOAT("Float", null), FILE("File", (FormsInterface) new FormTypeFileController().getController()), STRING("String", null);
    
    private FormsInterface controller;
    private String name;
    private String type;
    private String input;
    private String output;
    private String decimalPlaces;

    private FieldType(String type, FormsInterface controller) {
        this.controller = controller;
        this.type = type;       
    }
    
    //Testes
//    private FieldType(String name, String type, String input, String output, String decimalPlaces, FormsInterface controller) {
//        this.controller = controller;
//        this.name = name;
//        this.type = type;        
//        this.input = input;
//        this.output = output;
//        this.decimalPlaces = decimalPlaces;
//    }

    public FormsInterface getController() {
        return controller;
    }

    public void setController(FormsInterface controller) {
        this.controller = controller;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(String decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
}
