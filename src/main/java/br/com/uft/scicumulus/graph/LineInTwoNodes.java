/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

/**
 *
 * @author Frederico da Silva Santos
 */
public class LineInTwoNodes extends Line{
    private Scene scene;
    private Node nodeStart;
    private Node nodeEnd;

    OnRemoveEvent removeEvent;

    public LineInTwoNodes(Scene scene, OnRemoveEvent cancelEvent) {
        this.scene = scene;
        this.removeEvent = cancelEvent;
    }

    public void setNodeStart(Node nodeStart) {
        this.nodeStart = nodeStart;
        toBack();
        Center center = new Center(this.nodeStart);
        startXProperty().bind(center.centerXProperty());
        startYProperty().bind(center.centerYProperty());
        setEndX(center.centerXProperty().doubleValue());
        setEndY(center.centerYProperty().doubleValue());
        scene.setOnMouseMoved((MouseEvent me) -> {
            setEndX(me.getX());
            setEndY(me.getY());
        });
        scene.setOnKeyPressed((KeyEvent kp) -> {
            if (kp.getCode() == KeyCode.ESCAPE) {
                scene.setOnMouseMoved(null);
                removeEvent.remove(this);
            }
        });
    }

    public void setNodeEnd(Node nodeEnd) {
        this.nodeEnd = nodeEnd;
        Center center = new Center(nodeEnd);
        endXProperty().bind(center.centerXProperty());
        endYProperty().bind(center.centerYProperty());
        scene.setOnMouseMoved(null);
    }

    public Node getNodeStart() {
        return nodeStart;
    }

    public Node getNodeEnd() {
        return nodeEnd;
    }

    public interface OnRemoveEvent {

        public void remove(Line line);

    }
}
