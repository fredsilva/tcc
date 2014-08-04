/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.graph;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Activity extends BorderPane {

    /**
     * Representa uma Activity
     */    
    ConnectionPoint connectionPointLeft = new ConnectionPoint();
    ConnectionPoint connectionPointLeft2 = new ConnectionPoint();
    ConnectionPoint connectionPointRigth = new ConnectionPoint();
    ConnectionPoint connectionPointRigth2 = new ConnectionPoint();    
    ConnectionPoint connectionPointBottom = new ConnectionPoint();
    ConnectionPoint connectionPointBottom2 = new ConnectionPoint();    
    Label topLbl = new Label("Activity");
    
    HBox topHb = new HBox();
    VBox leftVb = new VBox();
    VBox rightVb = new VBox();
    HBox bottomVb = new HBox();
        
    
    public Activity() {                
        setPrefSize(150, 80);        
        setStyle("-fx-background-color: #09B367; -fx-border-color: #0E8335; -fx-border-style: solid; -fx-border-width: 2;");        
        topLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        
        topHb.getChildren().add(topLbl);
        topHb.setAlignment(Pos.CENTER);

//        leftVb.getChildren().add(connectionPointLeft);
//        leftVb.getChildren().add(connectionPointLeft2);
        leftVb.setAlignment(Pos.CENTER);
        leftVb.setStyle("-fx-padding: 5;-fx-spacing: 5;");        

//        rightVb.getChildren().add(connectionPointRigth);
//        rightVb.getChildren().add(connectionPointRigth2);        
        rightVb.setAlignment(Pos.CENTER);
        rightVb.setStyle("-fx-padding: 5;-fx-spacing: 5;");

//        bottomVb.getChildren().add(connectionPointBottom);
//        bottomVb.getChildren().add(connectionPointBottom2);
        bottomVb.setAlignment(Pos.CENTER);
        bottomVb.setStyle("-fx-padding: 5;-fx-spacing: 5;");
      
        setTop(topHb);
        setLeft(leftVb);
        setRight(rightVb);
        setBottom(bottomVb);        
    }        
    
    public void addPoint(String position, Node node){
        if (position.equals("rigth")){
            rightVb.getChildren().add(node);
        }
    }
}
