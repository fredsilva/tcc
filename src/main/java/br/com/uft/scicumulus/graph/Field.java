/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import br.com.uft.scicumulus.kryonet.FieldKryo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Frederico da Silva Santos
 *
 */
public class Field  implements Serializable{
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
    
    public List<Field> convert(List<FieldKryo> fieldsKryo) {
        //Converte um Field em FieldKryo
        List<Field> fields = new ArrayList<>();
        for (FieldKryo fieldKryo : fieldsKryo) {
//            FieldKryo fieldKryo = new FieldKryo();
            this.setName(fieldKryo.getName());
            this.setType(fieldKryo.getType());
            this.setOperation(fieldKryo.getOperation());
            this.setDecimalPlaces(fieldKryo.getDecimalPlaces());
            this.setInput(fieldKryo.getInput());
            this.setOutput(fieldKryo.getOutput());
            fields.add(this);
        }
        return fields;
    }
 
    @Override
    public String toString(){
        return  name;
    }
}
