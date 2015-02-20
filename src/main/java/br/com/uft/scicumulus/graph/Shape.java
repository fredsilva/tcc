/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import java.security.NoSuchAlgorithmException;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
    float positionX;
    float positionY;
            
    public Shape() throws NoSuchAlgorithmException {        
//        setIdObject();
        object = new Rectangle();
        object.setStrokeWidth(2);
        title = new Label();     
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        getChildren().addAll(object, title);
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    
//    public String getIdObject() {
//        return idObject;
//    }

//    public void setIdObject() throws NoSuchAlgorithmException {
//        Random random = new Random();
//        String input = Integer.toString(random.nextInt(1000)) + "-" + new Date();
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//        md.reset();
//        byte[] buffer = input.getBytes();
//        md.update(buffer);
//        byte[] digest = md.digest();
//        String id = "";
//        for (int i = 0; i < digest.length; i++) {
//            id += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
//        }
//        this.idObject = id;
//    }
        
    
    public void onMouseClicked(){
        this.object.setFill(Color.GOLD.deriveColor(0, 1.2, 1, 0.6));        
        this.object.setStroke(Color.GOLDENROD);
    }    
    
    public void onMouseExit(){
       this.object.setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1.2, 1, 0.6));        
       this.object.setStroke(Color.CORNFLOWERBLUE);         
    }
}
