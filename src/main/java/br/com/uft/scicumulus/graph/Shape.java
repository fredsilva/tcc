/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Frederico da Silva Santos
 */

/**
 * 
 * Classe base para desenhar os objetos na tela
 */
public class Shape extends StackPane{
    
    Rectangle object;
    Label title;
            
    public Shape() {        
        object = new Rectangle();
        object.setStrokeWidth(2);
        title = new Label();     
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        getChildren().addAll(object, title);
    }
}
