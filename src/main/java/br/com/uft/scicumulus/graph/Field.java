/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import br.com.uft.scicumulus.enums.FieldType;


/**
 *
 * @author Frederico da Silva Santos
 *
 */
public class Field {
    String name;
    String operation;
    String type;
    String input, output;
    String decimalPlaces;

    public Field() {
    }

    public Field(Field field){
        this.name = field.getName();
        this.type = field.getType();
        this.operation = field.getOperation();
        this.input = field.getInput();
        this.output = field.getOutput();
        this.decimalPlaces = field.getDecimalPlaces();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
