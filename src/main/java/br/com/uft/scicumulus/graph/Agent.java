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
public class Agent extends Shape{
    private String name;
    private TYPE type;  
    Activity wasAssociatedWith;

    public Agent() {
    }
        
    public Agent(String name, TYPE type) {
        this.name = name;
        super.title.setText(name);
        this.type = type;        
        setDataAgent();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }        

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }   

    public Activity getWasAssociatedWith() {
        return wasAssociatedWith;
    }

    public void setWasAssociatedWith(Activity wasAssociatedWith) {
        this.wasAssociatedWith = wasAssociatedWith;
    }
    
    public void setDataAgent(){            
        super.object.setWidth(70);
        super.object.setHeight(70);
        super.object.setArcWidth(70);
        super.object.setArcHeight(70);                
        
        if (this.type.equals(Agent.TYPE.HARDWARE)){
            super.object.setFill(Color.GRAY.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.GRAY);
        }
        if (this.type.equals(Agent.TYPE.ORGANIZATION)){
            super.object.setFill(Color.DARKCYAN.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.DARKCYAN);
        }
        if (this.type.equals(Agent.TYPE.SOFTWARE)){
            super.object.setFill(Color.LIMEGREEN.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.LIMEGREEN);
        }
        if (this.type.equals(Agent.TYPE.USER)){
            super.object.setFill(Color.MEDIUMPURPLE.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.MEDIUMPURPLE);
        }                       
    }
    
    public enum TYPE{
        ORGANIZATION, USER, SOFTWARE, HARDWARE;
    }
}
