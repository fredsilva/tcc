/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Relation;

/**
 *
 * @author fredsilva
 */
public class RelationKryo {    
    String idObject;
    private String name;    
    public ActivityKryo nodeStart;
    public ActivityKryo nodeEnd;
    public double startX, startY, endX, endY;
        
    public RelationKryo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActivityKryo getNodeStart() {
        return nodeStart;
    }

    public void setNodeStart(ActivityKryo nodeStart) {
        this.nodeStart = nodeStart;
    }

    public ActivityKryo getNodeEnd() {
        return nodeEnd;
    }

    public void setNodeEnd(ActivityKryo nodeEnd) {
        this.nodeEnd = nodeEnd;
    }

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }
    
    public RelationKryo convert(Relation relation){
        //Converte uma Activity em activityKryo
        RelationKryo relationKryo = new RelationKryo();
        relationKryo.setIdObject(relation.getIdObject());
        relationKryo.setName(relation.getName());
        relationKryo.setStartX(relation.getStartX());
        relationKryo.setStartY(relation.getStartY());
        relationKryo.setEndX(relation.getEndX());
        relationKryo.setEndY(relation.getEndY());
        System.out.println("Convert - StartX: "+relationKryo.getStartX());
        System.out.println("Convert - EndX: "+relationKryo.getEndX());
        System.out.println("Convert - StartY: "+relationKryo.getStartY());
        System.out.println("Convert - EndY: "+relationKryo.getEndY());
        
        relationKryo.setNodeStart(new ActivityKryo().convert((Activity) relation.getNodeStart()));
        relationKryo.setNodeEnd(new ActivityKryo().convert((Activity) relation.getNodeEnd()));  
        
        return relationKryo;
    }
}
