/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus;

import br.com.javafx.interfaces.FxmlPathModel;

/**
 *
 * @author fredsilva
 */
public enum FxmlPATH implements FxmlPathModel {

    PRINCIPAL("/fxml/FXMLScicumulus.fxml", "TItulo");

    String titulo;
    String path;

    private FxmlPATH(String path, String titulo) {
        this.titulo = titulo;
        this.path = path;
    }

    
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getTitle() {
        return titulo;
    }

}
