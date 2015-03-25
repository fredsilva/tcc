/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.kryonet;

import br.com.uft.scicumulus.graph.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frederico da Silva Santos
 */
public class FieldKryo {

    String name;
    String operation;
    String type;
    String input, output;
    String decimalPlaces;

    public FieldKryo() {
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

    public List<FieldKryo> convert(List<Field> fields) {
        //Converte um Field em FieldKryo
        List<FieldKryo> fieldsKryo = new ArrayList<>();
        for (Field field : fields) {
            FieldKryo fieldKryo = new FieldKryo();
            fieldKryo.setName(field.getName());
            fieldKryo.setType(field.getType());
            fieldKryo.setOperation(field.getOperation());
            fieldKryo.setDecimalPlaces(field.getDecimalPlaces());
            fieldKryo.setInput(field.getInput());
            fieldKryo.setOutput(field.getOutput());
            fieldsKryo.add(fieldKryo);            
        }
        return fieldsKryo;
    }
}
