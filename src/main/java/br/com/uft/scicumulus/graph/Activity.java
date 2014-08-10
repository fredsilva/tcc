/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.graph;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
    private String name, login, password;
    private String tag, description, type, templatedir, activation;
    private boolean paralell;
    private Integer num_machines;
    private boolean cloud;    
    private List<LineInTwoNodes> relations = new ArrayList<>();
    
//    ConnectionPoint connectionPointLeft = new ConnectionPoint();
//    ConnectionPoint connectionPointLeft2 = new ConnectionPoint();
//    ConnectionPoint connectionPointRigth = new ConnectionPoint();
//    ConnectionPoint connectionPointRigth2 = new ConnectionPoint();    
//    ConnectionPoint connectionPointBottom = new ConnectionPoint();
//    ConnectionPoint connectionPointBottom2 = new ConnectionPoint();    
    Label topLbl;
    
    HBox topHb = new HBox();
    VBox leftVb = new VBox();
    VBox rightVb = new VBox();
    HBox bottomVb = new HBox();
        
    
    public Activity(String name) {                
        //Iniciando valores padrões para a Activity
        this.name = name;
        this.paralell = true;
        this.num_machines = 1;
        this.cloud = false;
        
        setPrefSize(150, 80);        
        setStyle("-fx-background-color: #09B367; -fx-border-color: #0E8335; -fx-border-style: solid; -fx-border-width: 2;");        
        
        topLbl = new Label(this.name);
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

    public String getName() {
        return name;
    }    
    
    public void setName(String name){
        this.name = name;
        topLbl.setText(this.name);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isParalell() {
        return paralell;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplatedir() {
        return templatedir;
    }

    public void setTemplatedir(String templatedir) {
        this.templatedir = templatedir;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public void setParalell(boolean paralell) {
        this.paralell = paralell;
    }

    public Integer getNum_machines() {
        return num_machines;
    }

    public void setNum_machines(Integer num_machines) {
        this.num_machines = num_machines;
    }
        
    public boolean isCloud() {
        return cloud;
    }

    public void setCloud(boolean cloud) {
        this.cloud = cloud;
    }        

    public List<LineInTwoNodes> getRelations() {
        return relations;
    }

    public void addRelations(LineInTwoNodes relation) {
        this.relations.add(relation);
    }        
    
    public void addPoint(String position, Node node){
        if (position.equals("rigth")){
            rightVb.getChildren().add(node);
        }
    }            
}
