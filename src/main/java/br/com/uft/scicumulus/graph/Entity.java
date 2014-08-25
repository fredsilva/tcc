/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Frederico da Silva Santos
 */
public final class Entity extends Shape{
    private String name;
    private ActivityOld used;
    private ActivityOld wasGeneratedBy;
    private Entity wasDerivatedBy;
    private Agent wasAttributedTo;
    private final TYPE type;
        
    
    public Entity(String name ,TYPE type, ActivityOld used, ActivityOld wasGeneratedBy, Entity wasDerivatedBy, Agent wasAttributedTo) {
        this.name = name;
        super.title.setText(name);
        this.type = type;
        this.used = used;
        this.wasGeneratedBy = wasGeneratedBy;
        this.wasDerivatedBy = wasDerivatedBy;
        this.wasAttributedTo = wasAttributedTo;
        setDataEntity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActivityOld getUsed() {
        return used;
    }

    public void setUsed(ActivityOld used) {
        this.used = used;
    }
    
    public ActivityOld getWasGeneratedBy() {
        return wasGeneratedBy;
    }

    public void setWasGeneratedBy(ActivityOld wasGeneratedBy) {
        this.wasGeneratedBy = wasGeneratedBy;
    }

    public Entity getWasDerivatedBy() {
        return wasDerivatedBy;
    }

    public void setWasDerivatedBy(Entity wasDerivatedBy) {
        this.wasDerivatedBy = wasDerivatedBy;
    }

    public Agent getWasAttributedTo() {
        return wasAttributedTo;
    }

    public void setWasAttributedTo(Agent wasAttributedTo) {
        this.wasAttributedTo = wasAttributedTo;
    }            
        
    public void setDataEntity(){        
        super.object.setWidth(100);
        super.object.setHeight(50);
        super.object.setArcWidth(50);
        super.object.setArcHeight(50);            
        
        if (this.type.equals(TYPE.COMPUTER)){
            super.object.setFill(Color.BLUEVIOLET.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.BLUEVIOLET);
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
