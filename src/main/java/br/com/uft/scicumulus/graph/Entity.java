/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import javafx.scene.paint.Color;

/**
 *
 * @author Frederico da Silva Santos
 */
public final class Entity extends Shape{
    private String name;    
    private Activity wasGeneratedBy;
    private Entity wasDerivedFrom;
    private Agent wasAttributedTo;
    private TYPE type;
        
    
    public Entity(String name ,TYPE type, Activity wasGeneratedBy, Entity wasDerivatedBy, Agent wasAttributedTo) {
        this.name = name;
        super.title.setText(name);
        this.type = type;        
        this.wasGeneratedBy = wasGeneratedBy;
        this.wasDerivedFrom = wasDerivatedBy;
        this.wasAttributedTo = wasAttributedTo;
        setDataEntity();
    }

    public Entity() {
        setDataEntity();
    }        

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Activity getWasGeneratedBy() {
        return wasGeneratedBy;
    }

    public void setWasGeneratedBy(Activity wasGeneratedBy) {
        this.wasGeneratedBy = wasGeneratedBy;
    }

    public Entity getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasDerivedFrom(Entity wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }        

    public Entity getWasDerivatedBy() {
        return wasDerivedFrom;
    }

    public void setWasDerivatedBy(Entity wasDerivatedBy) {
        this.wasDerivedFrom = wasDerivatedBy;
    }

    public Agent getWasAttributedTo() {
        return wasAttributedTo;
    }

    public void setWasAttributedTo(Agent wasAttributedTo) {
        this.wasAttributedTo = wasAttributedTo;
    }            

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }
        
    public void setDataEntity(){        
        super.object.setWidth(100);
        super.object.setHeight(50);
        super.object.setArcWidth(50);
        super.object.setArcHeight(50);            
        
        if (this.type.equals(TYPE.COMPUTER)){
            super.object.setFill(Color.ANTIQUEWHITE.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.CORAL);
        }
        if (this.type.equals(TYPE.FILE)){
            super.object.setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.CORNFLOWERBLUE);
        }
        if (this.type.equals(TYPE.NOTE)){
            super.object.setFill(Color.DIMGREY.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.DIMGREY);
        }
        if (this.type.equals(TYPE.PARAMETER)){
            super.object.setFill(Color.INDIANRED.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.INDIANRED);
        }
        if (this.type.equals(TYPE.VIRTUAL_MACHINE)){
            super.object.setFill(Color.DARKSLATEGRAY.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.DARKSLATEGRAY);
        }                
    }
    
    public enum TYPE{
        FILE, COMPUTER, PARAMETER, NOTE, VIRTUAL_MACHINE;                                     
    }
}
