/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.graph;

import br.com.uft.scicumulus.kryonet.ActivityKryo;
import br.com.uft.scicumulus.kryonet.RelationKryo;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
public class Relation extends Line implements Serializable {

    String idObject;
    private Scene scene;
    private Node dragDropArea;
    private String name;
    public Node nodeStart;
    public Node nodeEnd;
    Polygon arrow;
    private List<OnRemoveEvent> listOnRemove = new ArrayList<>();

    double[] arrowShape = new double[]{0, 0, 10, 20, -10, 20};
    Arrow arrowRelation;

    public Relation() throws NoSuchAlgorithmException {
//        generationIdObject();
        setStroke(Color.DARKGRAY);
        setStrokeWidth(2);
    }

    public Relation(String name, Scene scene, Node dragDropArea, OnRemoveEvent cancelEvent) throws NoSuchAlgorithmException {
        this.name = name;
        this.scene = scene;
        setStroke(Color.DARKGRAY);
        setStrokeWidth(2);
        if (cancelEvent != null) {
            listOnRemove.add(cancelEvent);
        }
        this.dragDropArea = dragDropArea;
        generationIdObject();

//        arrowRelation = new Arrow(null, 0f, arrowShape);
    }

    public Relation(String name) throws NoSuchAlgorithmException {
        this.name = name;
        generationIdObject();
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
//        scene.setOnKeyPressed((KeyEvent kp) -> {
//            if (kp.getCode() == KeyCode.ESCAPE) {                
//                scene.setOnMouseMoved(null);
//                destroy();
//            }           
//        });        
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

    public Relation convert(RelationKryo relationKryo, Node dragAndDropArea, Activity actStart, Activity actEnd) throws NoSuchAlgorithmException {
        //Converte uma activityKryo em Activity
        Relation relation = new Relation();
        relation.dragDropArea = dragAndDropArea;        
        relation.setIdObject(relationKryo.getIdObject());        
        relation.setName(relationKryo.getName());        
        relation.setNodeStart(actStart);
        relation.setNodeEnd(actEnd);

        return relation;
    }

    public interface OnRemoveEvent {

        public void remove(Relation line);

    }

    public void destroy() {
        listOnRemove.stream().forEach(p -> p.remove(this));
    }

    public void addOnRemoveEvent(OnRemoveEvent onRemoveEvent) {
        if (onRemoveEvent != null) {
            listOnRemove.add(onRemoveEvent);
        }
    }

    public void onMouseClicked() {
        setStroke(Color.GOLDENROD);
        setStrokeWidth(3);
    }

    public void onMouseExit() {
        setStroke(Color.DARKGRAY);
        setStrokeWidth(2);
    }

    public void generationIdObject() throws NoSuchAlgorithmException {
        Random random = new Random();
        String input = Integer.toString(random.nextInt(1000)) + "-" + new Date();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();
        String id = "";
        for (int i = 0; i < digest.length; i++) {
            id += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        this.idObject = id;
    }

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public boolean equals(Relation relation) {
        if (this.idObject.equals(relation.getIdObject())) {
            return true;
        } else {
            return false;
        }
    }
}
