/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Activity2 extends Rectangle {

    /**
     * Representa uma Activity
     */    
    private String name, login, password;
    private String tag, description, type, templatedir, activation, input_filename, output_filename;
    private boolean paralell;
    private Integer num_machines;
    private boolean cloud;    
    private List<Relation> relations = new ArrayList<>();
    
    private Agent wasAssociatedWith;
    
//    Label topLbl;
//    
//    HBox topHb = new HBox();
//    VBox leftVb = new VBox();
//    VBox rightVb = new VBox();
//    HBox bottomVb = new HBox();
        
    
//    public Activity2(String name, Agent wasAssociatedWith) {                
//        //Iniciando valores padrões para a Activity
//        this.name = name;        
//        this.paralell = true;
//        this.num_machines = 1;
//        this.cloud = false;
//        
//        this.wasAssociatedWith = wasAssociatedWith;
//        
////        setPrefSize(120, 60);        
////        setStyle("-fx-background-color: #09B367; -fx-border-color: #0E8335; -fx-border-style: solid; -fx-border-width: 2;");        
////        
////        topLbl = new Label(this.name);
////        topLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 12));                        
////        
////        topHb.getChildren().add(topLbl);
////        topHb.setAlignment(Pos.CENTER);
////
////        leftVb.setAlignment(Pos.CENTER);
////        leftVb.setStyle("-fx-padding: 5;-fx-spacing: 5;");        
////
////        rightVb.setAlignment(Pos.CENTER);
////        rightVb.setStyle("-fx-padding: 5;-fx-spacing: 5;");
////
////        bottomVb.setAlignment(Pos.CENTER);
////        bottomVb.setStyle("-fx-padding: 5;-fx-spacing: 5;");
////      
////        setTop(topHb);
////        setLeft(leftVb);
////        setRight(rightVb);
////        setBottom(bottomVb);           
//        
//    }

    public Activity2(String toString, Agent agent) {
        setDataActivity();
        this.name = name;        
        this.paralell = true;
        this.num_machines = 1;
        this.cloud = false;
        
        this.wasAssociatedWith = wasAssociatedWith;
    }

    public String getName() {
        return name;
    }    
    
    public void setName(String name){
        this.name = name;
//        topLbl.setText(this.name);
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

    public String getInput_filename() {
        return input_filename;
    }

    public void setInput_filename(String input_filename) {
        this.input_filename = input_filename;
    }

    public String getOutput_filename() {
        return output_filename;
    }

    public void setOutput_filename(String output_filename) {
        this.output_filename = output_filename;
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

    public List<Relation> getRelations() {
        return relations;
    }

    public void addRelations(Relation relation) {
        this.relations.add(relation);
    }        
    
    public void addPoint(String position, Node node){
        if (position.equals("rigth")){
//            rightVb.getChildren().add(node);
        }
    }            

    public Agent getWasAssociatedWith() {
        return wasAssociatedWith;
    }

    public void setWasAssociatedWith(Agent wasAssociatedWith) {
        this.wasAssociatedWith = wasAssociatedWith;
    }        
    
    public void setDataActivity(){
        setWidth(100);
        setHeight(50);
        setArcWidth(8);
        setArcHeight(8);            
              
        setFill(Color.GREEN.deriveColor(0, 1.2, 1, 0.6));
        setStrokeWidth(2);
        setStroke(Color.GREEN);           
    }
}