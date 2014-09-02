/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Frederico da Silva Santos, Thaylon Guede Santos
 */


public class Relation extends Line{
    private Scene scene;
    private Node dragDropArea;
    private String name;    
    public Node nodeStart;
    public Node nodeEnd;
    Polygon arrow;    
    private List<OnRemoveEvent> listOnRemove = new ArrayList<>();
    

    public Relation(String name, Scene scene, Node dragDropArea, OnRemoveEvent cancelEvent) {
        this.name = name;
        this.scene = scene;
        setStroke(Color.DARKGRAY);
        setStrokeWidth(2);
        if (cancelEvent != null){
            listOnRemove.add(cancelEvent);
        }        
        this.dragDropArea = dragDropArea;
                
//        this.arrow = new Polygon(new double[]{
//            45, 10,
//            10, 80,
//            80, 80,
//        });                
//        this.arrow.setFill(Color.DARKGRAY);        
    }

    public Relation(String name) {
        this.name = name;
    }
    
    public void setNodeStart(Node nodeStart) {
        this.nodeStart = nodeStart;
        toBack();
        Center center = new Center(this.nodeStart);       
        startXProperty().bind(center.centerXProperty());
        startYProperty().bind(center.centerYProperty());
        setEndX(center.centerXProperty().doubleValue());
        setEndY(center.centerYProperty().doubleValue());
        dragDropArea.setOnMouseMoved((MouseEvent me) -> {
            setEndX(me.getX());
            setEndY(me.getY());   
            
        });
        scene.setOnKeyPressed((KeyEvent kp) -> {
            if (kp.getCode() == KeyCode.ESCAPE) {
                scene.setOnMouseMoved(null);
                destroy();
            }
        });
    }

    public void setNodeEnd(Node nodeEnd) {
        this.nodeEnd = nodeEnd;
        Center center = new Center(nodeEnd);
        endXProperty().bind(center.centerXProperty());
        endYProperty().bind(center.centerYProperty());
        dragDropArea.setOnMouseMoved(null);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Node getNodeStart() {
        return nodeStart;
    }

    public Node getNodeEnd() {
        return nodeEnd;
    }

    public interface OnRemoveEvent {
        public void remove(Relation line);

    }        
    
    public void destroy(){
        listOnRemove.stream().forEach(p->p.remove(this));                
    }
    
    public void addOnRemoveEvent(OnRemoveEvent onRemoveEvent){
        if (onRemoveEvent != null){
            listOnRemove.add(onRemoveEvent);
        }
    }
}
