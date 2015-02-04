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

    private FieldType(String name, FormsInterface controller) {
        this.controller = controller;
        this.name = name;
    }

    public FormsInterface getController() {
        return controller;
    }

    public void setController(FormsInterface controller) {
        this.controller = controller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
