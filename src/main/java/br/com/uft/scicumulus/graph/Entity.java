/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Frederico da Silva Santos
 */
public final class Entity extends Rectangle{
    private String title;
    private Activity used;
    private Activity wasGeneratedBy;
    private Entity wasDerivatedBy;
    private Agent wasAttributedTo;
    private final TYPE type;
        
    
    public Entity(String title ,TYPE type, Activity used, Activity wasGeneratedBy, Entity wasDerivatedBy, Agent wasAttributedTo) {
        this.title = title;
        this.type = type;
        this.used = used;
        this.wasGeneratedBy = wasGeneratedBy;
        this.wasDerivatedBy = wasDerivatedBy;
        this.wasAttributedTo = wasAttributedTo;
        setDataEntity();
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public Activity getUsed() {
        return used;
    }

    public void setUsed(Activity used) {
        this.used = used;
    }
    
    public Activity getWasGeneratedBy() {
        return wasGeneratedBy;
    }

    public void setWasGeneratedBy(Activity wasGeneratedBy) {
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
        setWidth(100);
        setHeight(50);
        setArcWidth(50);
        setArcHeight(50);            
        
        if (this.type.equals(TYPE.COMPUTER)){
            setFill(Color.BLUEVIOLET.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.BLUEVIOLET);
        }
        if (this.type.equals(TYPE.FILE)){
            setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.CORNFLOWERBLUE);
        }
        if (this.type.equals(TYPE.NOTE)){
            setFill(Color.DIMGREY.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.DIMGREY);
        }
        if (this.type.equals(TYPE.PARAMETER)){
            setFill(Color.INDIANRED.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.INDIANRED);
        }
        if (this.type.equals(TYPE.VIRTUAL_MACHINE)){
            setFill(Color.DARKSLATEGRAY.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.DARKSLATEGRAY);
        }
        
        setStrokeWidth(2);
    }
    
    public enum TYPE{
        FILE, COMPUTER, PARAMETER, NOTE, VIRTUAL_MACHINE;                                     
    }
}
